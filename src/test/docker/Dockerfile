FROM unicon/grouper-demo:2.3.0

MAINTAINER jgasper@unicon.net

ADD grouper.ui-2.3.0/ /opt/grouper.ui-2.3.0/
ADD provisioningTargetAttributes.gsh /
ADD grouper-ui.properties /opt/grouper.ui-2.3.0/conf/

RUN set -x; \
    service mysql start \
    && service slapd start \
    && cd /opt/grouper.ui-2.3.0 \
    && /opt/apache-ant-1.9.5/bin/ant war \
    && cp dist/grouper.war /opt/apache-tomcat-6.0.44/webapps \
    && rm -rf /opt/apache-tomcat-6.0.44/webapps/grouper/ \
    && rm -rf /opt/apache-tomcat-6.0.44/work/ \
    && cd /opt/grouper.apiBinary-2.3.0/ \
    && bin/gsh /provisioningTargetAttributes.gsh \
    && rm /provisioningTargetAttributes.gsh

EXPOSE 389 3306 8080

CMD ["run-grouper.sh"]
