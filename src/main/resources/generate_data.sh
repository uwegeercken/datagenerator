#!/bin/sh

###
# shell script to run the data generator java program
#
# three parameters are passed to the DataGenerator class:
#  1. path to the folder where the category files are located
#  2. name and path of the file where the row layout is defined
#  3. the number of rows to be generated
#
#  or
#
#  you can define the parameters in a file named:
#  datagenerator.properties
#
# last update: 2009-02-10
#
# by uwe geercken, uwe.geercken@datamelt.com
###

# folder where the category files are stored
folder_categories="categories/english"

# name and path to the xml row layout file
layout_file="rowlayout_ferrari_applicants.xml"

# number of rows to be generated, adjust it to your requirements
number_of_rows=1000

# how often the number of generated rows will be displayed
output_interval=100

# specify the output format of the data.
# 0 meaning regular (mixed) case (default), 1 meaning lowercase only
# and 2 meaning uppercase only
dataformat=0

# specify the set of possible characters to be used for randomly
# generated values. if you leave this parameter away, then lowercase
# characters plus uppercase characters plus numbers are used as possible
# values.
possible_values="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890[]!$+*%&"

# maximum year that is used when generating
# random dates. default maximum year is 2199
maximum_year=2016

# minimum year that is used when generating
# random dates. default minimum year is 1970
minimum_year=2014

# the output file, if you want to have the data in a file.
# put a comment in front of the line if you want the output
# to only be sent to the console
##output_file="generated_data.csv"

# to generate some output during processing add the
# parameter -v
# or remove it to get no output during processing

# the datagenerator jar file
generator_jar="datagenerator-1.0.jar"

# classpath for the program
# we have to include the current path and the jar file
class_path=".:${generator_jar}"

# run java with the parameters as listed above
java -cp ${class_path} com.datamelt.datagenerator.DataCreator -c=${folder_categories} -l=${layout_file} -n=${number_of_rows} -e=${output_interval} -p=${possible_values} -f=${dataformat} -m=${maximum_year} -i=${minimum_year}


# if you specify the arguments in a properties file: datagenerator.properties
# then you can call the program without any parameter, as follows:
# java -cp ${class_path} com.datamelt.datagenerator.DataCreator


