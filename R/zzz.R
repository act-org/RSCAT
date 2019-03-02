.onLoad <- function(libname, pkgname) {
  rJava::.jpackage(pkgname, lib.loc=libname)
  addResourcePath("images", system.file("shinyApp", "images", package = "RSCAT"))
  shinyjs::useShinyjs()
}