#!/usr/bin/env python

# Combines all the csv results file into one big results file.
# It also replaces the participant ID (always 7 in our silly simulation)
# with the random number generated in the file name.
#
# Der Aglomerator also fixes the incorrect error calculations. It makes report
# and true percent be out of 100 rather than 1 and recalculates the
# clevland and mcGill error

from sys import argv
from sys import exit
from math import log
from math import fabs

OUT_FILE = "allData.csv"
HEADER = "PartipantID,Index,Chart_Type,Vis,VisID,Error,TruePerc,ReportPerc\n"
ID_INDEX = 0
ERROR_INDEX = 5
TRUE_INDEX = 6
REP_INDEX = 7


def main():
	files = argv[1:]  # don't get name of this script

	agglom(files)


# Writes out the new agglomerated main file. Also switches out
# the participant ID with the randomly generated number used to
# save the original file
def agglom(files):
	filew = open(OUT_FILE, 'w')

	filew.write(HEADER)

	for f in files:
		newID = f.split("-")[1]
		newID = newID.split(".")[0]

		with open(f, 'r') as filer:
			for line in filer:
				if line.startswith("Part"):  # Header -- don't want this
					continue

				listL = line.strip().split(",")
				listL[ID_INDEX] = newID  # Assign new id

				# Scale to out of 100
				listL[TRUE_INDEX] = str(float(listL[TRUE_INDEX]) * 100)
				listL[REP_INDEX] = str(float(listL[REP_INDEX]) * 100)

				listL[ERROR_INDEX] = str(log((fabs(float(listL[REP_INDEX]) -
					float(listL[TRUE_INDEX])) + (1.0/8.0)),2))

				newLine = ",".join(listL)
				filew.write(newLine + "\n")


if __name__ == '__main__':
	main()