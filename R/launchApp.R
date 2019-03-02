#' Launches the shiny app to confiure and run CAT simulations.
#' 
#' @export launchApp
#' @examples
#' \dontrun{
#' launchApp()
#' } 
#' @import shiny
launchApp <- function() {
  shinyApp(ui = shinyAppUI, server = shinyAppServer)
}