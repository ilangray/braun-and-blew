#!/usr/bin/env Rscript

# Does the analysis for hypothesis 1 of the viz project. Do we need a second
# hypothesis? Ich weiss nicht.

# Read in table
allData <- read.csv(file="allData.csv",head=TRUE,sep=",")

# Trim table to be only viz type and CM error
cols <- c(3, 6)
allData <- allData[,cols]
allData