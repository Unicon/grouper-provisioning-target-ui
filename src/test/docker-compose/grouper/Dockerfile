FROM unicon/grouper-demo:2.3.0

MAINTAINER jgasper@unicon.net

COPY grouper-ui.properties /opt/tomcat/webapps/grouper/WEB-INF/classes/
COPY webapp/* /opt/tomcat/webapps/grouper/WEB-INF/
COPY lib/* /opt/tomcat/webapps/grouper/WEB-INF/lib/
COPY provisioningTargetAttributes.gsh /provisioningTargetAttributes.gsh


RUN (/usr/sbin/ns-slapd -D /etc/dirsrv/slapd-dir &) \
    && while ! curl -s ldap://localhost:389 > /dev/null; do echo waiting for ldap to start; sleep 1; done; \
    (mysqld_safe & ) \
    && while ! curl -s localhost:3306 > /dev/null; do echo waiting for mysqld to start; sleep 3; done; \
    rm -rf /opt/tomcat/work/ \
    && cd /opt/grouper.apiBinary-2.3.0/ \
    && bin/gsh /provisioningTargetAttributes.gsh \
    && rm /*.gsh

EXPOSE 389 3306 8080

CMD ["run-grouper.sh"]
