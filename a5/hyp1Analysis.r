#!/usr/bin/env Rscript

# Does the analysis for hypothesis 1 of the viz project. Do we need a second
# hypothesis? Ich weiss nicht.

# Read in table
allData <- read.csv(file="allData.csv",head=TRUE,sep=",")

# Trim table to be only viz type and CM error
cols <- c(3, 6)
allData <- allData[,cols]

# Partition the matrix based on the chart type
tm <- subset(allData, Chart_Type == 0)
sqtm <- subset(allData, Chart_Type == 1)

tmVals <- tm[,2]
sqtmVals <- sqtm[,2]

# Calculate normal distribution
tmAvg <- mean(tmVals)
tmStdev <- sd(tmVals)
tmLen <- length(tmVals)
tmError <- qnorm(0.975) * tmStdev/sqrt(tmLen)
tmLeft <- tmAvg - tmError
tmRight <- tmAvg + tmError
cat("TREE MAP\n")
cat(tmLeft, "<", tmAvg, "<", tmRight, "\n\n")

sqtmAvg <- mean(sqtmVals)
sqtmStdev <- sd(sqtmVals)
sqtmLen <- length(sqtmVals)
sqtmError <- qnorm(0.975) * sqtmStdev/sqrt(sqtmLen)
sqtmLeft <- sqtmAvg - sqtmError
sqtmRight <- sqtmAvg + sqtmError
cat("SQUARIFIED TREE MAP")
cat(sqtmLeft, "<", sqtmAvg, "<", sqtmRight, "\n\n")

# Plot bar chart
means <- c(tmAvg, sqtmAvg)
lefts <- c(tmLeft, sqtmLeft)
rights <- c(tmRight, sqtmRight)
pdf("mapsBarPlot.pdf")
bp <- barplot(means, names.arg = c("TM", "SQTM"), ylim = c(0, 3), main="Tree Map Error",
	ylab="Clevland McGill Error")
segments(bp, lefts, bp, rights, lwd=2)
segments(bp - .1, lefts, bp + 0.1, lefts, lwd=2)
segments(bp - .1, rights, bp + .1, rights, lwd=2)
dev.off()

# Test for normal distribution
tmNormal <- shapiro.test(tmVals)
sqtmNormal <- shapiro.test(sqtmVals)

cat("TM VALS:\n")
print(tmNormal[2])
print(tmNormal[1])
cat("SQTM VALS:\n")
print(sqtmNormal[2])
print(sqtmNormal[1])

t.test(tmVals, sqtmVals)

