import edu.internet2.middleware.grouper.*
import edu.internet2.middleware.grouper.app.externalSystem.GrouperExternalSystem
import edu.internet2.middleware.grouper.app.google.GoogleGrouperExternalSystem
import edu.internet2.middleware.grouper.app.loader.GrouperLoader
import edu.internet2.middleware.grouper.app.loader.GrouperLoaderConfig
import edu.internet2.middleware.grouper.app.provisioning.ProvisionableGroupSave
import edu.internet2.middleware.grouper.attr.AttributeDef
import edu.internet2.middleware.grouper.attr.AttributeDefName
import edu.internet2.middleware.grouper.attr.AttributeDefNameSave
import edu.internet2.middleware.grouper.attr.AttributeDefSave
import edu.internet2.middleware.grouper.attr.AttributeDefValueType
import edu.internet2.middleware.grouper.cfg.dbConfig.GrouperDbConfig
import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess
import edu.internet2.middleware.morphString.Morph
import edu.internet2.middleware.morphString.MorphStringConfig

/** Init configs */

def configs = [
        "subject.properties",
        "grouper-ui.properties",
        "grouper.properties",
        "grouper-loader.properties",
        "grouper.text.en.us.properties"
]

configs.each { config ->
    File file = new File("/opt/grouper/initscripts/properties/${config}.import")

    if (file.exists()) {
        def reader = new InputStreamReader(file.newInputStream())

        Properties propertiesToImport = new Properties()
        propertiesToImport.load(reader)

        GrouperDbConfig subjectConfig = new GrouperDbConfig().configFileName(config)

        propertiesToImport.each { prop ->
            subjectConfig.propertyName((String) prop.key).value(prop.value).store()
            println "${config}: ${prop.key} = ${prop.value}"
        }
    }
}

GrouperLoader.scheduleJobs()



/** Create test group, assign GID and provision */

Group group = new GroupSave().assignName("test:testGroup").assignCreateParentStemsIfNotExist(true).save()

['800000000', '800000001', '800000002'].each {
    def s = SubjectFinder.findById(it, true)
    group.addMember(s, false)
}
