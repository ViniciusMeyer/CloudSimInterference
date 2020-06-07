#rm(list=ls())
app_list <- list()
pm_list <- list()

#importing interference data from each source file
app <- function (app_file){
  #print(app_file)
  df_inter <- read.csv2(app_file, sep=";")
  inter<-data.frame(df_inter[,1],df_inter[,2],df_inter[,3],df_inter[,4],df_inter[,5],df_inter[,6],df_inter[,7])
  inter<-setNames(inter,c("netp","nets","blk","mbw","llcmr","llcocc","cpu"))
  return(inter)
}

#add app in list
app_list_add <- function (app){
  app_list <<-c(app_list,app(app))
  #append(app_list, app(app))
  #return(list(app_list,app))
}

#add pm in list
pm_list_add <- function (qnt,size){
  pm_list <<-c(pm_list, c(qnt,size))
}

#input data
input_data <- function(file){
  input<- data.frame()
  input <- read.csv2(file, sep=" ")
  for(i in 1:nrow(input)){
    if (input[i,1]=="app"){
      app_list_add(toString(input[i,3]))
    }else if (input[i,1]=="pm"){
      pm_list_add(as.numeric(input[i,2]),as.numeric(toString(input[i,3])))
    }else{
      print("Error importing input.txt file")
    }
  }
  
  return(list(app_list, pm_list))
}
