#' Sets up the required JAR dependancy to parse Mosel.
#' 
#' @param path A character string specifying the xprm.jar file in the installed 
#'   Xpress folder.
#' @examples
#' \dontrun{
#' setupXprm(C:/xpressmp/lib/xprm.jar)
#' } 
#' @export
setupXprm <- function(path) {
  javaPath <- system.file("java", package = "RSCAT")
  file.copy(path, paste(javaPath, "xprm.jar", sep = "/"))
}