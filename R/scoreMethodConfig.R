#' Creates a scoring method configuration for CAT simulation
#' 
#' This is a gneric function to create a scoring method configuration from a
#' speicfic estimation algorithm configuration.
#' 
#' @param object an S4 object for the estimation algorithm configuration
#' @return the object of scoring method configuration which is an instance of
#'   \code{org/act/rscat/cat/ScoringMethodConfig}
#' @examples
#' \dontrun{
#' eapConfig <- EAPConfig(numQuad = 6L, minQuad = -2, maxQuad = 2, 
#'   priorDistType = "Normal", distParams = c(0,1))
#' scoreMethodConfig <- scoreMethodConfig(eapConfig)  
#' }
#' @import methods
#' @export
setGeneric(
          name = "scoreMethodConfig",
          def = function(object) {
            standardGeneric("scoreMethodConfig")
          })


#' @rdname scoreMethodConfig
#' @include EAPConfig.R
#' @import rJava
#' @export
setMethod(
          f = "scoreMethodConfig",
          signature = c("EAPConfig"),
          definition = function(object) {
            rJava::.jinit()
            if (object@priorDistType == "Normal") {
            distJava <- rJava::new(rJava::J("org/act/rscat/util/UniDimNormalDistribution"),
              object@distParams[1], object@distParams[2])
            } else if (object@priorDistType == "Uniform") {
              distJava <- rJava::new(rJava::J("org/act/rscat/util/UniDimUniformDistribution"),
                object@distParams[1], object@distParams[2])
            }
            configJava <- rJava::new(rJava::J("org/act/rscat/cat/ScoringMethodConfigEap"), 
              object@numQuad, object@minQuad, object@maxQuad, 
              rJava::.jcast(distJava, "org/act/rscat/util/ProbDistribution"))
            return(configJava)
          } 
          )
