#' Defines server logic to configure and run CAT simulations.
#' @param input an object that stores the current values of all of the widgets 
#'   in the app.
#' @param output an object that stores instructions for building the R objects
#'   in the app.
#' @import ggplot2 rJava
#' @importFrom utils read.csv
#' @importFrom shinyjs enable reset
shinyAppServer <- function(input, output) {
  
  # Creat objects for configurations
  observeEvent(input$runSim, {
    output$simResultSummary <- NULL
    output$simResultSummary <- renderText(isolate({
      # Validate item pool data
      validate(need(
        !is.null(input$itemPoolFile) && 
          itemUploadState$value != "reset",
        "Please select an item pool csv.")
      )

      # Simulation
      showNotification("Simulation is started.", duration = NULL)
      if (!is.null(input$itemPoolFile) && 
        itemUploadState$value != "reset") {
        itemPoolPath <- gsub("\\\\", "/", input$itemPoolFile$datapath)
        itemPoolColName <- colnames(read.csv(itemPoolPath, check.names = FALSE))
        numericCol <- strsplit(input$itemNumericColumns, ",")[[1]]
        itemNumericColumn <- logical(length(itemPoolColName))
        itemNumericColumn[itemPoolColName %in% numericCol] <- TRUE
      } else {
        itemPoolPath <- ""
        itemNumericColumn <- logical(0)
      }
      if (!is.null(input$passagePoolFile) && 
        passageUploadState$value != "reset") {
        passagePoolPath <- gsub("\\\\", "/", input$passagePoolFile$datapath)
        minNumPassage <- as.integer(input$minNumPassage)
        maxNumPassage <- as.integer(input$maxNumPassage)
        minNumItemPerPassage <- as.integer(input$minNumItemPerPassage)
        maxNumItemPerPassage <- as.integer(input$maxNumItemPerPassage)
        passagePoolColName <- colnames(read.csv(passagePoolPath, 
          check.names = FALSE))
        numericCol <- strsplit(input$passageNumericColumns, ",")[[1]]
        passageNumericColumn <- logical(length(passagePoolColName))
        passageNumericColumn[passagePoolColName %in% numericCol] <- TRUE
      } else {
        passagePoolPath <- ""
        minNumPassage <- 0L
        maxNumPassage <- 0L
        minNumItemPerPassage <- 0L
        maxNumItemPerPassage <- 0L
        passageNumericColumn <- logical(0)
      }
      if (!is.null(input$constraintFile) && 
        constraintUploadState$value != "reset") {
        constraintPath <- gsub("\\\\", "/", input$constraintFile$datapath)
      } else {
        constraintPath <- ""
      }
      if (input$scoringAdvanced) {
        numQuad <- as.integer(input$numPoints)
        minQuad <- as.numeric(input$minPoint)
        maxQuad <- as.numeric(input$maxPoint)
        priorDistType <- input$priorDist
        if (priorDistType == "Normal") {
          priorDistParams <- c(as.numeric(input$distMean),
                               as.numeric(input$distSd))
        } else if (priorDistType == "Uniform") {
          priorDistParams <- c(as.numeric(input$aVal), as.numeric(input$bVal))
        }
      } else {
        
        # Default configurations
        numQuad <- 8L
        minQuad <- -4.0
        maxQuad <- 4.0
        priorDistType <- "Normal"
        priorDistParams <- c(0, 1)
      }

      # Solver configuration
      solverConfig <- SolverConfig(absGap = 1e-3, relGap = 1e-3, intTol = 1e-6)

      # EAP configuration
      eapConfig <- EAPConfig(
        numQuad = numQuad,
        minQuad = minQuad,
        maxQuad = maxQuad,
        priorDistType = priorDistType,
        distParams = priorDistParams
      )

      # CAT configuration
      catConfig <- CATConfig(
        solverConfig = solverConfig,
        itemSelectionMethod = input$itemSelectionMethod,
        scoreMethodConfig = scoreMethodConfig(eapConfig),
        lValue = input$lValue,
        exposureControlType = input$exposureType,
        exposureControlRate = input$goalRate
      )

      # Test configuration
      testConfig <- TestConfig(
        testConfigID = input$testName,
        testLength = input$testLength,
        itempoolPath = itemPoolPath,
        passagepoolPath = passagePoolPath,
        constraintPath = constraintPath,
        itemNumericColumn = itemNumericColumn,
        passageNumericColumn = passageNumericColumn,
        numPassageLB = minNumPassage, numPassageUB = maxNumPassage,
        numItemPerPassageLB = minNumItemPerPassage,
        numItemPerPassageUB = maxNumItemPerPassage
      )

      # Simulation configuration
      if (input$trueThetePriorDistType == "Uniform") {
        trueThetaDistParams <- c(as.numeric(input$thetaMin),
                                 as.numeric(input$thetaMax))
      } else if (input$trueThetePriorDistType == "Normal") {
        trueThetaDistParams <- c(as.numeric(input$thetaMean),
                                 as.numeric(input$thetaSD))
      }
      simConfig <- SimConfig(
        simID = input$simName,
        numExaminees = as.integer(input$simTestTakerNum),
        trueThetaDistType = input$trueThetePriorDistType,
        trueThetaDistParams = trueThetaDistParams
      )

      # Run simulation and return results
      simResult <- runSim(catConfig, testConfig, simConfig)
      showNotification("Simulation is done.", duration = NULL)
      simResultSummary <- summary(simResult)

      # Downloadable csv of simulation result
      output$downloadCSV <- downloadHandler(
        filename = function() {
          paste(input$simName, "_result", ".csv", sep = "")
        },
        content = function(file) {
          result2CSV(simResult, file)
        }

      )
      shinyjs::enable("downloadCSV")
      return(simResultSummary$msg)
    }))
  }
  )
  
  output$itemPoolSummary <- renderTable({
    result <-
      data.frame(ItemNum = as.integer(0), PassageNum = as.integer(0))
    if (!is.null(input$itemPoolFile) && itemUploadState$value != "reset") {
    #if (!is.null(input$itemPoolFile)) {
      itempoolDf <- read.csv(input$itemPoolFile$datapath)
      itemNum <- nrow(itempoolDf)
      result$ItemNum <- itemNum
    } else {
      result$ItemNum <- as.integer(0)
    }
    if (!is.null(input$passagePoolFile) && passageUploadState$value != "reset") {
      passagepoolDf <- read.csv(input$passagePoolFile$datapath)
      passageNum <- nrow(passagepoolDf)
      result$PassageNum <- passageNum
    } else {
      result$PassageNum <- as.integer(0)
    }
    return(result)
  })
  
  output$isItemPoolUploaded <- reactive({
    return (!is.null(input$itemPoolFile) && 
      itemUploadState$value != "reset")
  })
  output$isPassagePoolUploaded <- reactive({
    return (!is.null(input$passagePoolFile) && 
      passageUploadState$value != "reset")
  })
  output$isConstraintUploaded <- reactive({
    return (!is.null(input$constraintFile) && 
      constraintUploadState$value != "reset")
  })
  output$isShownSummary <- reactive({
    return (input$runSim > 0)
  })
  
  trueThetaDistParams <- reactive({
    if (input$trueThetePriorDistType == "Uniform") {
      trueThetaDistParams <- c(as.numeric(input$thetaMin),
        as.numeric(input$thetaMax))
    } else if (input$trueThetePriorDistType == "Normal") {
      trueThetaDistParams <- c(as.numeric(input$thetaMean),
        as.numeric(input$thetaSD))
    }
  })
  
  outputOptions(output, 'isItemPoolUploaded', suspendWhenHidden=FALSE)
  outputOptions(output, 'isPassagePoolUploaded', suspendWhenHidden=FALSE)
  outputOptions(output, 'isConstraintUploaded', suspendWhenHidden=FALSE)
  outputOptions(output, 'isShownSummary', suspendWhenHidden=FALSE)
  
  # Generate a summary of the itempool
  output$paramDistPlot <- renderPlot({
    if (is.null(input$itemPoolFile))
      return(NULL)
    itempoolDf <- read.csv(input$itemPoolFile$datapath)
    
    fig1.param.a <- ggplot(NULL, aes(itempoolDf$A.Param)) +
      geom_histogram(binwidth = 0.1,
      color = "black",
      fill = "white") +
      xlim(0, 2) +
      xlab(expression(italic(a)[i])) +
      ylab("Count") +
      theme_classic() +
      theme(text = element_text(family = "serif", size = 18))
    
    fig1.param.b <- ggplot(NULL, aes(itempoolDf$B.Param)) +
      geom_histogram(binwidth = 0.2,
                     color = "black",
                     fill = "white") +
      xlim(-5, 5) +
      xlab(expression(italic(b)[i])) +
      ylab("Count") +
      theme_classic() +
      theme(text = element_text(family = "serif", size = 18))
    
    fig1.param.c <- ggplot(NULL, aes(itempoolDf$C.Param)) +
      geom_histogram(binwidth = 0.05,
                     color = "black",
                     fill = "white") +
      xlim(0, 1) +
      xlab(expression(italic(c)[i])) +
      ylab("Count") +
      theme_classic() +
      theme(text = element_text(family = "serif", size = 18))
    
    gridExtra::grid.arrange(
      fig1.param.a,
      fig1.param.b,
      fig1.param.c,
      nrow = 1,
      ncol = 3,
      bottom = grid::textGrob(
        "Distributions of item parameters in the operational pool",
        gp = grid::gpar(fontsize = 18, fontfamily = "serif")
      )
    )
  })
  itemUploadState <- reactiveValues(
    value = ""
  )
  passageUploadState <- reactiveValues(
    value = ""
  )
  constraintUploadState <- reactiveValues(
    value = ""
  )
  observeEvent(input$itemPoolFile, {
    itemUploadState$value <- "uploaded"
  })
  observeEvent(input$passagePoolFile, {
    passageUploadState$value <- "uploaded"
  })
  observeEvent(input$constraintFile, {
    constraintUploadState$value <- "uploaded"
  })
  output$constraintSummary <- renderTable({
    result <- data.frame(ID = as.integer(), Description = as.character())
    if (!is.null(input$constraintFile) && constraintUploadState$value != "reset") {
      constraintDf <- read.csv(input$constraintFile$datapath)
      result <- rbind(result, constraintDf[c("Id", "Description","IsLoaded")])
    } else {
      result <- data.frame(ID = as.integer(), Description = as.character())
    }
    return(result)
  })
  
  output$itemDistStat <- renderText({"Upload an item pool to display the distribution."})
  
  observeEvent(input$resetInput, {
    itemUploadState$value <- "reset"
    passageUploadState$value <- "reset"
    constraintUploadState$value <- "reset"
    output$simResultSummary <- NULL
    resetElementIds <- c("testName", "testLength", "itemPoolFile", 
      "passagePoolFile", "minNumPassage", "maxNumPassage", "constraintFile", 
      "enemyItmesStr", "itemSelectionMethod", "scoringMethod", "scoringAdvanced",
      "numPoints", "minPoint", "maxPoint", "priorDist", "distMean", "distSd", 
      "aVal", "bVal", "exposureType", "goalRate", "simName", "simTestTakerNum",
      "trueThetePriorDistType", "thetaMin", "thetaMax", "thetaMean", "thetaSD",
      "lValue")
    lapply(resetElementIds, shinyjs::reset)
  })
}