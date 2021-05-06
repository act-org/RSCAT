#' JAR dependency setup.
#' 
#' This function downloads JAR dependency from mvnrepository and put them in
#' /java
#' @examples 
#' \dontrun{
#' setupJars()
#' }
#' @importFrom utils download.file
#' @export
setupJars <- function() {
  javaPath <- system.file("java", package = "RSCAT")
  download.file("https://repo1.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar", 
                paste(javaPath, "commons-io-2.6.jar", sep = "/"), mode = "wb")
  download.file("https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.8.1/commons-lang3-3.8.1.jar", 
                paste(javaPath, "commons-lang3-3.8.1.jar", sep = "/"), mode = "wb")
  download.file("https://repo1.maven.org/maven2/org/apache/commons/commons-math3/3.6.1/commons-math3-3.6.1.jar", 
                paste(javaPath, "commons-math3-3.6.1.jar", sep = "/"), mode = "wb")
  download.file("https://repo1.maven.org/maven2/log4j/log4j/1.2.17/log4j-1.2.17.jar", 
                paste(javaPath, "log4j-1.2.17.jar", sep = "/"), mode = "wb")
  download.file("https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.26/slf4j-api-1.7.26.jar", 
                paste(javaPath, "slf4j-api-1.7.26.jar", sep = "/"), mode = "wb")
  download.file("https://repo1.maven.org/maven2/org/slf4j/slf4j-log4j12/1.7.26/slf4j-log4j12-1.7.26.jar", 
                paste(javaPath, "slf4j-log4j12-1.7.26.jar", sep = "/"), mode = "wb")
}