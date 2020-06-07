#change_point_detection

#determining applications intervals to classify
cpd_interval <- function(app_list){
  interval_list <- list()
  for(i in 0:(length(app_list)/7-1)){
    
    a=prcomp(app_from_list(app_list,i)[,1:7] ,scale = FALSE)
    a.man=cpt.var(a$x[,1],method='PELT',penalty='MBIC',pen.value='2*log(n)')
    interval_list<-c(interval_list, list(cpts(a.man)))
  }
  
  return(interval_list)
}



#https://cran.r-project.org/web/packages/changepoint/index.html

#also
#https://cran.r-project.org/web/packages/cpm/index.html
#https://cran.r-project.org/web/packages/bcp/index.html
#https://cran.r-project.org/web/packages/ecp/index.html
