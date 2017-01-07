FROM unicon/grouper-demo:2.3.0

MAINTAINER jgasper@unicon.net

COPY grouper-ui.properties /opt/apache-tomcat-6.0.44/webapps/grouper/WEB-INF/classes/
COPY webapp/* /opt/apache-tomcat-6.0.44/webapps/grouper/WEB-INF/
COPY lib/* /opt/apache-tomcat-6.0.44/webapps/grouper/WEB-INF/lib/
COPY provisioningTargetAttributes.gsh /provisioningTargetAttributes.gsh


RUN set -x; \
    rm -rf /opt/apache-tomcat-6.0.44/work/ \
    && service mysql start \
    && service slapd start \
    && cd /opt/grouper.apiBinary-2.3.0/ \
    && bin/gsh /provisioningTargetAttributes.gsh \
    && rm /*.gsh

EXPOSE 389 3306 8080

CMD ["run-grouper.sh"]
