#' Sets up JAR dependency.
#' 
#' This function downloads JAR dependencies securely through https and put them in
#' /java
#' @examples 
#' \dontrun{
#' setupJars()
#' }
#' @importFrom utils download.file
#' @export
setupJars <- function() {
  javaPath <- system.file("java", package = "RSCAT")
  dependencyList <- list()
  dependencyList[["commons-io"]] <- list(url = "https://www-us.apache.org/dist//commons/io/binaries/commons-io-2.6-bin.zip",
    files = c("commons-io-2.6/commons-io-2.6.jar"))
  dependencyList[["commons-lang"]] <- list(url = "https://www-us.apache.org/dist//commons/lang/binaries/commons-lang3-3.8.1-bin.zip",
    files = c("commons-lang3-3.8.1/commons-lang3-3.8.1.jar"))
  dependencyList[["commons-math3"]] <- list(url = "https://www-us.apache.org/dist//commons/math/binaries/commons-math3-3.6.1-bin.zip",
    files = c("commons-math3-3.6.1/commons-math3-3.6.1.jar"))
  dependencyList[["apache-log4j"]] <- list(url = "https://www-us.apache.org/dist/logging/log4j/1.2.17/log4j-1.2.17.zip",
    files = c("apache-log4j-1.2.17/log4j-1.2.17.jar"))
  dependencyList[["slf4j"]] <- list(url = "https://www.slf4j.org/dist/slf4j-1.7.26.zip",
    files = c("slf4j-1.7.26/slf4j-api-1.7.26.jar", "slf4j-1.7.26/slf4j-log4j12-1.7.26.jar"))
  lapply(dependencyList, FUN = processDependency, javaPath)
}

#' Downloads and processes JAR dependency.
#' @param dependency the list that contains information of dependency to install.
#' @param dir the directory where the dependency is to be installed.
#' @importFrom utils download.file unzip
processDependency <- function(dependency, dir) {
  fileName <- basename(dependency[["url"]])
  download.file(dependency[["url"]], 
                paste(dir, fileName, sep = "/"), mode = "wb")
  unzip(paste(dir, fileName, sep = "/"), files = dependency[["files"]],
        junkpaths = TRUE, overwrite = TRUE, exdir = dir)
}