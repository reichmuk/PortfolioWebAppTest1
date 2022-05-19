FROM tomcat:9
ADD target/PortfolioWebApp-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/PortfolioWebApp-1.0-SNAPSHOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]