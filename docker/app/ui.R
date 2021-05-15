#' Defines UI for CAT simulations.
#'
#'@importFrom shinycssloaders withSpinner
#'@importFrom shinyjs useShinyjs disabled

shinyAppUI <- fluidPage(
  shinyjs::useShinyjs(),
  tags$head(
    tags$style(HTML("hr {border-top: 1px solid #000000;}"))
  ),

  tags$img(src = "images/RSCAT_logo.png", width = 200),
  titlePanel(title = HTML("Configuration and Simulation"), windowTitle = "RSCAT"),
  
  # Sidebar layout
  sidebarLayout(
    
    # Sidebar panel for inputs
    sidebarPanel(
      h4("Test Configuration"),
      
      # Specify the test name
      textInput("testName", "Test Name"),
      
      # Specify the test length
      numericInput("testLength", "Test Length", 10),
      
      fileInput(
        "itemPoolFile",
        "Choose Item Pool CSV File",
        multiple = FALSE,
        accept = c("text/csv", "text/comma-separated-values,text/plain", ".csv")
      ),
      
      # Specify item numeric columns
      conditionalPanel(condition = "output.isItemPoolUploaded == true",
        textAreaInput("itemNumericColumns", "Specify Numeric Columns
        (delimited by ,)")
      ),
      fileInput(
        "passagePoolFile",
        "Choose Passage Pool CSV File",
        multiple = FALSE,
        accept = c("text/csv", "text/comma-separated-values,text/plain", ".csv")
      ),
      
      # Specify passage number range if passage pool is uploaded
      conditionalPanel(condition = "output.isPassagePoolUploaded == true",
        textAreaInput("passageNumericColumns", "Specify Numeric Columns
        (delimited by ,)"),
        wellPanel(
          h5("Number of Passages"),
          splitLayout(
            textInput("minNumPassage", "Min", 0),
            textInput("maxNumPassage", "Max", 10)),
          h5("Number of Items Per Passage"),
          splitLayout(
            textInput("minNumItemPerPassage", "Min", 1),
            textInput("maxNumItemPerPassage", "Max", 10))
        )
      ),
      
      fileInput(
        "constraintFile",
        "Choose Constraint CSV File",
        multiple = FALSE,
        accept = c("text/csv", "text/comma-separated-values,text/plain", ".csv")
      ),
      
      # Specify the enemy item constraints
      conditionalPanel(condition = "input.enableEnemy",
        textAreaInput("enemyItmesStr", "Specify Enemy Item Sets", 
        "e.g., {ID1,ID3,ID5},{ID10,ID3}","100%")
      ),
      
      hr(),
      h4("CAT Algorithm Configuration"),
      
      # Select the scoring method
      selectInput("scoringMethod", "Scoring Method", c("EAP" = "eap")),
      
      # Specify if advanced options are enabled for the scoring method
      checkboxInput("scoringAdvanced", "Advanced Options", FALSE),
      
      conditionalPanel(
        condition = "input.scoringAdvanced",
        wellPanel(
          h5("Quadrature Points"),
          splitLayout(
            textInput("numPoints", "Points", 8),
            textInput("minPoint", "Min", -4),
            textInput("maxPoint", "Max", 4)
          ),
          h5("Prior Distribution"),
          splitLayout(
            selectInput(
              "priorDist",
              "Type",
              c("Normal" = "Normal", "Uniform" = "Uniform")
            )
          ),
          conditionalPanel(condition = "input.priorDist == 'Normal'",
            splitLayout(
              textInput("distMean", 'Mean', 0),
              textInput("distSd", 'SD', 1))),
          conditionalPanel(condition = "input.priorDist == 'Uniform'",
            splitLayout(
              textInput("aVal", 'a', -4),
              textInput("bVal", 'b', 4))),
          tags$head(tags$style(
            HTML("
                 .shiny-split-layout > div {
                 overflow: visible;
                 }
                 ")
            ))
            )
            ),
      # Select the exposure control type
      selectInput("exposureType", "Exposure Control Rate", 
        c("None" = "None", "Item" = "Item")),
      
      # Specify the exposure control parameters
      conditionalPanel(condition = "input.exposureType != 'None'",
        numericInput("goalRate", "Goal Rate",0.5, 0, 1, 0.05)
      ),
      
      # Randomize first L items
      numericInput("lValue", "Randomize First L Item Selection", 0),
      
      hr(),
      h4("Simulation Configuration"),
      
      # Specify the simulation name
      textInput("simName", "Simulation Name"),
      
      # Specify the number of simulated test takers
      numericInput("simTestTakerNum", "Number of Examinees", 10, min = 1, 
                   max = 10000),
      
      # Select the true ability distribution type
      selectInput("trueThetePriorDistType", "True Ability Distribution", 
        c("Normal" = "Normal", "Uniform" = "Uniform")),
      
      # Specify parameter
      conditionalPanel(
        condition = "input.trueThetePriorDistType == 'Uniform'",
        wellPanel(
          splitLayout(
            textInput("thetaMin", 'Theta Min', -4),
            textInput("thetaMax", 'Max', 4)
          )
        ),
        tags$head(tags$style(
          HTML("
               .shiny-split-layout > div {
               overflow: visible;
               }
               ")
          ))
          ),
      
      # Specify parameters for normal distribution
      conditionalPanel(
        condition = "input.trueThetePriorDistType == 'Normal'",
        wellPanel(
          splitLayout(
            textInput("thetaMean", 'Mean', 0),
            textInput("thetaSD", 'SD', 1)
          )
        ),
        tags$head(tags$style(
          HTML("
               .shiny-split-layout > div {
               overflow: visible;
               }
               ")
          ))
          ),
      
      # Button to run simulation
      actionButton("runSim", "Run Simulation"),
      
      # Download simulation result
      shinyjs::disabled(downloadButton("downloadCSV", "Download Result")),
      
      # Reset all input
      actionButton("resetInput", "Reset")
    ),
    
    # Main panel for displaying outputs
    mainPanel(
      h4("Item Pool Summary"),
      wellPanel(
        tableOutput("itemPoolSummary")
      ),
      h4("Item Parameter Distribution"),
      wellPanel(
      conditionalPanel(condition = "output.isItemPoolUploaded == true",
        plotOutput(outputId = "paramDistPlot", height="300px")),
      conditionalPanel(condition = "output.isItemPoolUploaded == false",
        verbatimTextOutput("itemDistStat"))),
      h4("Constraint Summary"),
      wellPanel(
        tableOutput("constraintSummary")
      ),
      h4("Simulation Result Summary"),
      conditionalPanel(condition = "output.isShownSummary == true",
        shinycssloaders::withSpinner(verbatimTextOutput("simResultSummary"), 
        type = 5)
      )
    )
  )
)
