context("Sim Test")
library(testthat)
library(RSCAT)

skip_on_cran <- function() {
  if (identical(Sys.getenv("NOT_CRAN"), "true")) {
    return(invisible(TRUE))
  }
  
  skip("On CRAN")
}

test_that("Simple Simulation with discrete item pool", {
  skip_on_cran()
  itemNumericColumn <- c(FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE, 
    TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE, FALSE, FALSE, 
    FALSE, FALSE, TRUE, FALSE, TRUE, FALSE, FALSE,FALSE)
  itemPoolCSVPath <- system.file("extdata", "itempool10Items.csv", 
    package = "RSCAT")
  constraintCSVPath <- system.file("extdata", "constraintSet1.csv", 
    package = "RSCAT")
  solverConfig <- SolverConfig(absGap = 1e-3, relGap = 1e-3, intTol = 1e-6)
  eapConfig <- EAPConfig(numQuad = 6L, minQuad = -2, maxQuad = 2, 
    priorDistType = "Normal", distParams = c(0,1))
  catConfig <- CATConfig(solverConfig = solverConfig, 
    scoreMethodConfig = scoreMethodConfig(eapConfig), lValue = 3L)
  testConfig <- TestConfig(testConfigID = "Test1", testLength = 6L,
    itempoolPath = itemPoolCSVPath, constraintPath = constraintCSVPath,
    itemNumericColumn = itemNumericColumn)
  simConfig <- SimConfig(simID = "Sim1", numExaminees = 10L,
    trueThetaDistType = "Normal", trueThetaDistParams = c(0,1))
  simResult <- runSim(catConfig, testConfig, simConfig)
  expect_equal(simResult@numExaminees, 10L)
  expect_equal(length(simResult@trueThetas), 10L)
  expect_equal(length(simResult@shadowTests), 10L)
  expect_equal(length(simResult@shadowTests[[1]]), 6L)
  })

test_that("Simulation with item pool and passage pool", {
  skip_on_cran()
  itemNumericColumn <- c(FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, TRUE, 
    TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE, TRUE,
    FALSE, FALSE, FALSE, FALSE, TRUE, FALSE, TRUE, FALSE, 
    FALSE,FALSE)
  passageNumericColumn <- c(FALSE, TRUE, TRUE, FALSE, FALSE)
  itemPoolCSVPath <- system.file("extdata", "itemPool720Items.csv", 
    package = "RSCAT")
  passagePoolCSVPath <- system.file("extdata", "passagePool30Passages.csv", 
    package = "RSCAT")
  constraintCSVPath <- system.file("extdata", "constraintSet2.csv", 
    package = "RSCAT")
  solverConfig <- SolverConfig(absGap = 1e-3, relGap = 1e-3, intTol = 1e-6)
  eapConfig <- EAPConfig(numQuad = 8L, minQuad = -2, maxQuad = 2, 
    priorDistType = "Normal", distParams = c(0,1))
  catConfig <- CATConfig(solverConfig = solverConfig, 
  scoreMethodConfig = scoreMethodConfig(eapConfig), lValue = 5L)
  testConfig <- TestConfig(testConfigID = "Test2", testLength = 20L, 
    itempoolPath = itemPoolCSVPath,
    passagepoolPath = passagePoolCSVPath,
    constraintPath = constraintCSVPath, 
    itemNumericColumn = itemNumericColumn,
    passageNumericColumn = passageNumericColumn,
    numPassageLB = 3L, numPassageUB = 5L,
    numItemPerPassageLB = 1L, numItemPerPassageUB = 10L)
    simConfig <- SimConfig(simID = "Sim1", numExaminees = 5L)
    simResult <- runSim(catConfig, testConfig, simConfig)
    expect_equal(simResult@numExaminees, 5L)
    expect_equal(length(simResult@trueThetas), 5L)
    expect_equal(length(simResult@shadowTests), 5L)
    expect_equal(length(simResult@shadowTests[[1]]), 20L)
  })