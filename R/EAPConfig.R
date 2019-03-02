#' EAP configuration
#' 
#' An S4 class to represent expected A posteriori (EAP) scoring algorithm 
#' configuration.
#' 
#' An instance of this S4 class can be applied to the generic function 
#' \code{scoreMethodConfig} to create an Java object for scoring method 
#' configuration.
#' 
#' @slot numQuad a positive integer specifying the number of quadrature points
#' @slot minQuad a numeric value specifying the minimum quadrature point
#' @slot maxQuad a numeric value specifying the maximum quadrature point
#' @slot priorDistType a character string specifying the prior distribution of 
#'   ability. "Normal" for Normal distribution and "Uniform" for uniform 
#'   distribution.
#' @slot distParams a numeric vector specifying parameters of the prior
#'   distribution. (mean, sd) for the Normal distribution, (a, b) for the 
#'   uniform distribution.
#' @import methods
#' @export EAPConfig
EAPConfig <- setClass(
  "EAPConfig",
  slots = c(
    numQuad = "integer",
    minQuad = "numeric",
    maxQuad = "numeric",
    priorDistType = "character",
    distParams = "numeric"
  ),
  prototype = list(
    numQuad = 8L,
    minQuad = -4.0,
    maxQuad = 4.0,
    priorDistType = "Normal",
    distParams = c(0.0, 1.0)
  ),
  validity = function(object) {
    if (object@numQuad <= 0) {
      return("numQuad should be a positve integer value.")
    } else if (object@maxQuad <= object@minQuad) {
      return("The maxQuad should be greater than minQuad.")
    } else if (!object@priorDistType %in% c("Normal", "Uniform")) {
      return("The priorDistType can only be \"Normal\" or \"Uniform\".")
    }
    
    if (length(object@distParams) != 2) {
      return("The distParams should conaints two values.")
    }
    if (object@priorDistType == "Normal" &&
        object@distParams[2] <= 0) {
      return("Invalid standard deviation is specified.")
    } else if (object@priorDistType == "Uniform" &&
               object@distParams[2] <= object@distParams[1]) {
      return("Invalid distribution parameters are specificed.")
    }
    return(TRUE)
  }
)