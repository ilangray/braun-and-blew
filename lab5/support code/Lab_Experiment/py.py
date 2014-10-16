#!/usr/bin/env python

from sys import argv

files = argv[1:]

data = {}

for f in files:
        with open(f, 'r') as filer:
                for line in filer:
                        if line.startswith("P"):
                                continue
                        listL = line.strip().split(",")

                        if listL[2] not in data.keys():
                                data[listL[2]] = [listL[5]]
                        else:
                                data[listL[2]].append(listL[5])


toReturn = {}

for key in data.keys():
        l = data[key]
        avg = 0.0
        for x in l:
                avg += float(x)

        avg /= len(l)
        toReturn[key] = avg


print toReturn

