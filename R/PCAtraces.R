cTracesPCA<-function(dataset,start,end){

  pca<-list()
  
 
  
  for(x in 1:length(dataset)){
    
    a<- prcomp(dataset[[x]][start:end,c(7,3,4,6,1,2)])
    pca[[x]]<-a$x[,1]
    }

  trace.pca<-data.frame(pca)
  
  #mudar ou MINSEP(nao proporcional) ou >xxx (regra da janela deslizante)

  
  output<- onlineCPD(trace.pca, multivariate = TRUE, minsep = 1, maxsep = 10^10)
  
  
  result<-list()
  current<-output$changepoint_lists$maxCPs[[1]][1]
  result<-c(result,current)
  #print(which.max(output$changepoint_lists$maxCPs[[1]]))
    for(i in output$changepoint_lists$maxCPs[[1]][2]:which.max(output$changepoint_lists$maxCPs[[1]])){
    
    #print(output$changepoint_lists$maxCPs[[1]][i])
    if((output$changepoint_lists$maxCPs[[1]][i])-current>300){
      current<-output$changepoint_lists$maxCPs[[1]][i]
      result<-c(result,current)
      #print(current)
      
    }
  } 
#print(result)
  return(result)
}