FROM rocker/shiny:latest

# Install Java
RUN apt-get -y update && apt-get install -y \
  openjdk-8-jdk \
  && apt-get clean \
  && R CMD javareconf

# Extract Xpress installation files
ADD xp*setup.tar /root

# Install Xpress with community license
RUN ./root/xp*setup/install.sh -l community -d /opt/xpressmp -k no

# Download and install required R packages
RUN R -e "install.packages(c('shinycssloaders', 'shinyjs', 'rJava', 'Metrics', 'ggplot2', 'gridExtra', 'grid', 'methods', 'stats', 'utils', 'RSCAT'))"

COPY shiny-server.sh /usr/bin/shiny-server.sh
RUN chmod -R 755 /usr/bin

# Copy the app
RUN mkdir /srv/shiny-server/rscat-app
COPY app /srv/shiny-server/rscat-app

# Make all app files readable
RUN chmod -R 755 /srv/shiny-server

# Download java dependency
RUN mkdir jars
RUN wget https://repo1.maven.org/maven2/commons-io/commons-io/2.7/commons-io-2.7.jar -P jars
RUN wget https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.8.1/commons-lang3-3.8.1.jar -P jars
RUN wget https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar -P jars
RUN wget https://repo1.maven.org/maven2/log4j/log4j/1.2.17/log4j-1.2.17.jar -P jars
RUN wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.26/slf4j-api-1.7.26.jar -P jars
RUN wget https://repo1.maven.org/maven2/org/slf4j/slf4j-log4j12/1.7.26/slf4j-log4j12-1.7.26.jar -P jars

# Copy and link jars
RUN cp jars -RT /usr/local/lib/R/site-library/RSCAT/java
RUN ln -s /opt/xpressmp/lib/xprm.jar /usr/local/lib/R/site-library/RSCAT/java

# Set environment variables
ENV XPRESSDIR=/opt/xpressmp
ENV XPRESS=/opt/xpressmp/bin
ENV PATH=${XPRESSDIR}/bin:${PATH}
ENV LD_LIBRARY_PATH=${XPRESSDIR}/lib
ENV DYLD_LIBRARY_PATH=${XPRESSDIR}/lib:${DYLD_LIBRARY_PATH}
ENV SHLIB_PATH=${XPRESSDIR}/lib:${SHLIB_PATH}
ENV LIBPATH=${XPRESSDIR}/lib:${LIBPATH}
ENV CLASSPATH=${XPRESSDIR}/lib/xprs.jar:${CLASSPATH}
ENV CLASSPATH=${XPRESSDIR}/lib/xprb.jar:${CLASSPATH}
ENV CLASSPATH=${XPRESSDIR}/lib/xprm.jar:${CLASSPATH}
ENV XPAUTH_PATH=${XPRESSDIR}/bin/xpauth.xpr
ENV MOSEL_DSO=$XPRESSDIR/dso

# Add /opt/xpressmp/lib to R_LD_LIBRARY_PATH in ldpaths
RUN sed -i 's/^: ${R_LD_LIBRARY_PATH.*\/usr\/local\/lib.*/: ${R_LD_LIBRARY_PATH=${R_HOME}\/lib:\/usr\/local\/lib:\/usr\/lib\/x86_64-linux-gnu:\/opt\/xpressmp\/lib}/' /usr/local/lib/R/etc/ldpaths

EXPOSE 3838

CMD ["/usr/bin/shiny-server.sh"]
