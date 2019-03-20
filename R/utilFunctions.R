#' Generates CAT simulation summary 
#' 
#' @param object an object of \code{SimResult}.
#' Generates the summary report of CAT simulation.
#' @export
setMethod(
  f = "summary",
  signature = c("SimResult"),
  definition = function(object) {
    thetaBias <- Metrics::bias(object@trueThetas, object@finalThetas)
    thetaRMSE <- Metrics::rmse(object@trueThetas, object@finalThetas)
    thetaBias <- format(round(thetaBias, 4), nsmall = 4)
    thetaRMSE <- format(round(thetaRMSE, 4), nsmall = 4)
    metrics <- list(thetaBias = thetaBias, thetaRMSE = thetaRMSE)
    msg <- paste("Numer of simulated examinees: ", 
      object@numExaminees, "\n",
      "Theta estimate bias: ", thetaBias, "\n",
      "Theta estimate RMSE: ", thetaRMSE, "\n", sep = "")
    cat(msg)
    ret <- list(msg = msg, metrics = metrics)
    return(ret)
  }
)

#' Creates a simulation result CSV file.
#' 
#' @param simResult an instance of S4 class \code{SimResult}.
#' @param file a writable connection or a character string naming the file 
#'   to write to.
#' @importFrom stats sd 
#' @importFrom utils write.csv
#' @export
result2CSV <- function(simResult, file) {
  sink(file)
  
  # Calculate and generate overall simulation metrics
  numExaminees <- simResult@numExaminees
  trueThetaMean <- format(round(mean(simResult@trueThetas), 4), nsmall = 4)
  trueThetaSD <- format(round(sd(simResult@trueThetas), 4), nsmall = 4)
  thetaBias <- Metrics::bias(simResult@trueThetas, simResult@finalThetas)
  thetaRMSE <- Metrics::rmse(simResult@trueThetas, simResult@finalThetas)
  thetaBias <- format(round(thetaBias, 4), nsmall = 4)
  thetaRMSE <- format(round(thetaRMSE, 4), nsmall = 4)
  overallMetrics <- data.frame("Examinee Number" = numExaminees, 
    "True Theta Mean" = trueThetaMean, "True Theta SD" = trueThetaSD, 
    "Theta Estimate Bias" = thetaBias, "Theta Estimate RMSE" = thetaRMSE)
  write.csv(overallMetrics, row.names = FALSE)
  cat("\n\n")
  
  # Calculate and generate metrics for individual examinees
  examineeOverall <- list()
  examineeStage <- list()
  testLength <- length(simResult@estThetas[[1]])
  for (n in 1:numExaminees) {
    
    # Overall metrics for each examinee
    examineeOverall[[n]] <- data.frame("Examinee ID" = as.character(n), 
      "True Theta" = simResult@trueThetas[n], 
      "Final Theta Estimate" = simResult@finalThetas[n], 
      "Final Theta Estimate SE" = simResult@finalThetaSEs[n],
      "Administered Items" = paste('"', paste(simResult@itemsAdministered[[n]], 
      collapse = ","), '"', sep = ""))
    
    # Detailed metrics at each stage
      examineeStage[[n]] <- data.frame("Stage" = as.character(1:testLength), 
        "Administered Item" = simResult@itemsAdministered[[n]], 
        "Score" = simResult@scores[[n]], 
        "Theta Estimate" = simResult@estThetas[[n]],
        "Theta Estimate SE" = simResult@estThetaSEs[[n]], 
        "Shadow Test" = sapply(simResult@shadowTests[[n]], pasteWithQuote))
      write.csv(examineeOverall[[n]], row.names = FALSE)
      write.csv(examineeStage[[n]], row.names = FALSE)
      cat("\n\n")
  }
  sink()
}

pasteWithQuote <- function(x) {
  ret <- paste('"', paste(x, collapse = ","), '"', sep = "")
}
