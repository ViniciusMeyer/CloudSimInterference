#install.package("changepoint")
#install.package("cpm")
#install.package("bcp")
#install.package("ecp")



#install.packages("changepoint")
#install.packages("dplyr")
#install.packages("caret")
#install.packages("stringr")
#install.packages("dplyr")


#rm(list=ls())

library("changepoint")
library("e1071")
library("caret")
library("stringr")
library("dplyr")

input_file <- paste("~/interference/r/simulation","/input.txt", sep = "")
#training_dataset_folder <- "~/interference/r/simulation/forced/"

#source("~/interference/r/simulation/input_dataset.R")
#total<-input_dataset(training_dataset_folder)

#source("~/interference/r/simulation/misc.R")
#source("~/interference/r/simulation/input.R")
#source("~/interference/r/simulation/cpd.R")
#source("~/interference/r/simulation/kmeans.R")
#source("~/interference/r/simulation/svm.R")


#import datacenter information
input_list <- input_data(input_file)
app_list <- input_list[[1]]
pm_list <- input_list[[2]]

#change point detection
interval_list <- cpd_interval(app_list)

#setting commom intervals #falta limitar cada aplicação .... 
interval_list<-commom_interval(interval_list,app_list)

#Interference-aware Classifier
class_ref <- svm_classifier(app_list, interval_list)

#Runn
