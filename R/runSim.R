#' Run CAT simulations
#' 
#' \code{runSim} runs CAT simulations based on the provided configurations and 
#' returns the simulation result.
#' 
#' This function calls the Java helper method \code{org.act.util.RHelper.runSim}
#' via rJava to exceute CAT simulation. 
#' 
#' @param catConfig an instance of the S4 class \code{CATConfig} for CAT 
#'   configurations.
#' @param testConfig an instance of the S4 class \code{TestConfig} for test 
#'   specification configuration.
#' @param simConfig an instance of the S4 class \code{SimConfig} for test 
#'   specification configuration.
#' @return the simulation result in the instance of \code{SimResult}.
#' @examples
#' if(interactive()){
#' ## Defines item attributes types
#' itemNumericColumn <- c(FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE, 
#'   TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, FALSE, 
#'   FALSE, TRUE, FALSE, TRUE, FALSE, FALSE,FALSE)
#' 
#' ## Specifies the item pool file 
#' itemPoolCSVPath <- system.file("extdata", "itempool10Items.csv", 
#'   package = "RSCAT")
#' 
#' ## Specifies the constraint table file   
#' constraintCSVPath <- system.file("extdata", "constraintSet1.csv", 
#'   package = "RSCAT")
#'
#' ## Configures solver parameters  
#' solverConfig <- SolverConfig(absGap = 1e-3, relGap = 1e-3, intTol = 1e-6)
#' 
#' ## Configures the EAP estimaition 
#' eapConfig <- EAPConfig(numQuad = 6L, minQuad = -2, maxQuad = 2, 
#'   priorDistType = "Normal", distParams = c(0,1))
#'   
#' ## Configures CAT   
#' catConfig <- CATConfig(solverConfig = solverConfig, 
#'   scoreMethodConfig = scoreMethodConfig(eapConfig), lValue = 3L)
#'   
#' ## Configures test specifications   
#' testConfig <- TestConfig(testConfigID = "Test1", testLength = 6L, 
#'   itempoolPath = itemPoolCSVPath, constraintPath = constraintCSVPath, 
#'   itemNumericColumn = itemNumericColumn)
#'
#' ## Configures the simulation      
#' simConfig <- SimConfig(simID = "Sim1", numExaminees = 8L)
#' 
#' ## Runs CAT simulation
#' simResult <- runSim(catConfig, testConfig, simConfig)
#' }
#' @import rJava
#' @export    
runSim <- function(catConfig, testConfig, simConfig) {

  # Call the Java help method to run the simulation
  obj <- rJava::.jnew("org/act/util/RHelper")
  result <-
    rJava::.jcall(
      obj,
      returnSig = "Ljava/util/List;",
      method = "runSim",
      rJava::.jcast(
        catConfig@scoreMethodConfig,
        "org/act/cat/AbstractScoringMethodConfig"
      ),
      catConfig@initialTheta,
      catConfig@scalingConstant,
      catConfig@itemSelectionMethod,
      catConfig@exposureControlType,
      catConfig@exposureControlRate,
      catConfig@lValue,
      catConfig@solverConfig@absGap,
      catConfig@solverConfig@relGap,
      catConfig@solverConfig@intTol,
      FALSE,
      testConfig@testConfigID,
      testConfig@testLength,
      testConfig@itempoolPath,
      testConfig@passagepoolPath,
      testConfig@constraintPath,
      testConfig@itemNumericColumn,
      testConfig@passageNumericColumn,
      FALSE,
      testConfig@numPassageLB,
      testConfig@numPassageUB,
      testConfig@numItemPerPassageLB,
      testConfig@numItemPerPassageUB,
      simConfig@simID,
      simConfig@numExaminees,
      simConfig@trueThetaDistType,
      simConfig@trueThetaDistParams
    )
  
  numExaminees <- simConfig@numExaminees
  trueThetas <- numeric(numExaminees)
  finalThetas <- numeric(numExaminees)
  finalThetaSEs <- numeric(numExaminees)
  estThetasList <- list()
  estThetaSEsList <- list()
  scoresList <- list()
  itemAdministeredList <- list()
  shadowTestsList <- list()
  engineTimeList <- list()
  
  # Retrive data from the Java object
  for (i in 1:numExaminees) {
    resultExaminee <- result$get(as.integer(i - 1))
    trueThetas[i] <- resultExaminee$getTrueTheta()
    finalThetas[i] <- resultExaminee$getFinalTheta()$getTheta()
    finalThetaSEs[i] <- resultExaminee$getFinalTheta()$getSe()
    thetas <- numeric(testConfig@testLength)
    thetaSEs <- numeric(testConfig@testLength)
    scores <- numeric(testConfig@testLength)
    itemsAdmin <- character(testConfig@testLength)
    shadowTestExamineeList <- list()
	  engineTime <- numeric(testConfig@testLength)
    
    for (j in 1:testConfig@testLength) {
      
      # For theat estimate and estimate se, the first element is the initial
      # value
      thetas[j] <-
        resultExaminee$getThetaEstList()$get(as.integer(j))$getTheta()
      thetaSEs[j] <-
        resultExaminee$getThetaEstList()$get(as.integer(j))$getSe()
      scores[j] <-
        resultExaminee$getItemScoresList()$get(as.integer(j - 1))$getItemScores()
      itemsAdmin[j] <-
        resultExaminee$getItemsAdministered()$get(as.integer(j - 1))
      shadowTestExamineeList[[j]] <-
        character(testConfig@testLength)
      resultExamineeStage <- resultExaminee$getShadowTestList()$get(as.integer(j - 1))
      shadowTestExamineeList[[j]] <- 
        unlist(strsplit(gsub("\\[|\\]", "", resultExamineeStage$toString()), ", "))
	    engineTime[j] <-
        resultExaminee$getCatEngineTimeList()$get(as.integer(j-1))
    }
    estThetasList[[i]] <- thetas
    estThetaSEsList[[i]] <- thetaSEs
    scoresList[[i]] <- scores
    itemAdministeredList[[i]] <- itemsAdmin
    shadowTestsList[[i]] <- shadowTestExamineeList
    engineTimeList[[i]] <- engineTime
  }
  return(
    SimResult(
      numExaminees = numExaminees,
      trueThetas = trueThetas,
      finalThetas = finalThetas,
      finalThetaSEs = finalThetaSEs,
      estThetas = estThetasList,
      estThetaSEs = estThetaSEsList,
      scores = scoresList,
      itemsAdministered = itemAdministeredList,
      shadowTests = shadowTestsList,
      engineTime = engineTimeList
    )
  )
  
}