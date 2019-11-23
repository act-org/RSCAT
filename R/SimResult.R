#' CAT simulation result
#' 
#' An S4 class to represent CAT simulation results.
#' 
#' @slot numExaminees a positive integer representing the number of simulated 
#'   examinees.
#' @slot trueThetas a numeric vector representing the true theta values of 
#'   simulated examinees.
#' @slot finalThetas a numeric vector representing the final theta estimates of 
#'   simulated examinees.
#' @slot finalThetaSEs a numeric vector representing the final theta estimate 
#'   standard errors (SEs) of simulated examinees.
#' @slot estThetas a list of length \code{numExaminees}. Each element of the 
#'   list is a numeric vector representing theta estimate at adaptive 
#'   stages for the simulated examinee.
#' @slot estThetaSEs a list of length \code{numExaminees}. Each element of the 
#'   list is a numeric vector representing theta estimate standard error (SE) 
#'   at adaptive stages for the simulated examinee.
#' @slot scores a list of length \code{numExaminees}. Each element of the 
#'   list is a numeric vector representing scores at adaptive stages for the 
#'   simulated examinee. 0 for an incorrect repsonse and 1 for 
#'   a correct respone.
#' @slot itemsAdministered a list of length \code{numExaminees}. Each element 
#'   of the list is a character vector representing identifiers of adminsitered 
#'   items at adaptive stages for the simulated examinee.
#' @slot shadowTests a list of length \code{numExaminees}. Each element of
#'   the list is also a list representing the shadow test assembled at each 
#'   adaptive stage.
#' @slot engineTime a list of length \code{numExaminees}. Each element of
#'   the list is a numeric vector representing the engine time at each adaptive step.
#'	 the engine time includes time consumed by CAT algorithms and shadow test assembly.
SimResult <- setClass(
  "SimResult",
  slots = c(
            numExaminees = "integer",
            trueThetas = "numeric",
            finalThetas = "numeric",
            finalThetaSEs = "numeric",
            estThetas = "list",
            estThetaSEs = "list",
            scores = "list",
            itemsAdministered = "list",
            shadowTests = "list",
			      engineTime = "list")
)