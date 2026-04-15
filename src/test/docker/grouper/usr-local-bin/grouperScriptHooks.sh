#!/bin/bash

# called after the setupFiles functions is called, almost before the process starts
grouperScriptHooks_setupFilesPostChown() {
  pushd /opt/grouper/grouperWebapp/WEB-INF
  directory="/opt/grouper/patch"
  found_files=0
  for script in "$directory"/*.tgz; do
    if [ -e "$script" ]; then
      found_files=1
      echo "============================================================================================================ ..."
      echo "*** Patching $PWD with $script ..."
      echo "============================================================================================================ ..."
      tar xzf $script
      tar tzf $script | xargs chown tomcat:tomcat
    fi
  done
  popd

  if [ $found_files -eq 0 ]; then
    echo "No .tgz files found in $directory"
  fi
  

  if [ -n "${DEMO_RUN_GSH_INIT+set}" ]; then
    echo "DEMO_RUN_GSH_INIT; INFO: (grouperScriptHooks.sh-body) running grouperScriptHooks_setupFilesPostChown"

    directory="/opt/grouper/initscripts"
    found_files=0
    for script in "$directory"/*.gsh "$directory"/*.groovy; do
        if [ -e "$script" ]; then
          found_files=1
          echo "============================================================================================================ ..."
          echo "*** DEMO_RUN_GSH_INIT; INFO: (grouperScriptHooks.sh-body) run gsh script $script ..."
          echo "============================================================================================================ ..."

          sudo --preserve-env -u tomcat /opt/grouper/grouperWebapp/WEB-INF/bin/gsh.sh $script
          echo "*** DEMO_RUN_GSH_INIT; INFO: (grouperScriptHooks.sh-body) gsh result=$?"
        fi
    done

    if [ $found_files -eq 0 ]; then
      echo "No .gsh or .groovy files found in $directory"
    fi
  else
    echo "DEMO_RUN_GSH_INIT != true; INFO: (grouperScriptHooks.sh-body) NOT running grouperScriptHooks_setupFilesPostChown"
  fi
}
