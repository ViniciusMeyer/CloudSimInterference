  input_dataset <- function(folder_source){
    
  
  
  #importing cache
  df_cache <- read.csv2(paste(folder_source,"cache100.csv", sep = ""), sep=";")
  cache<-data.frame(df_cache[,1],df_cache[,2],df_cache[,3],df_cache[,4],df_cache[,5],df_cache[,6],df_cache[,7],"cache")
  cache<-setNames(cache,c("netp","nets","blk","mbw","llcmr","llcocc","cpu","category"))
  
  #importing cache miss
  df_cache_miss <- read.csv2(paste(folder_source,"cache_miss.csv", sep = ""), sep=";")
  cache_miss<-data.frame(df_cache_miss[,1],df_cache_miss[,2],df_cache_miss[,3],df_cache_miss[,4],df_cache_miss[,5],df_cache_miss[,6],df_cache_miss[,7],"miss")
  cache_miss<-setNames(cache_miss,c("netp","nets","blk","mbw","llcmr","llcocc","cpu","category"))
  
  #importing memory
  df_memory <- read.csv2(paste(folder_source,"memory100.csv", sep = ""), sep=";")
  mem<-data.frame(df_memory[,1],df_memory[,2],df_memory[,3],df_memory[,4],df_memory[,5],df_memory[,6],df_memory[,7],"mem")
  mem<-setNames(mem,c("netp","nets","blk","mbw","llcmr","llcocc","cpu","category"))
  
  #importing cpu
  df_cpu <- read.csv2(paste(folder_source,"cpu100.csv", sep = ""), sep=";")
  cpu<-data.frame(df_cpu[,1],df_cpu[,2],df_cpu[,3],df_cpu[,4],df_cpu[,5],df_cpu[,6],df_cpu[,7],"cpu")
  cpu<-setNames(cpu,c("netp","nets","blk","mbw","llcmr","llcocc","cpu","category"))
  
  #importing disk
  df_disk <- read.csv2(paste(folder_source,"disk100.csv", sep = ""), sep=";")
  disk<-data.frame(df_disk[,1],df_disk[,2],df_disk[,3],df_disk[,4],df_disk[,5],df_disk[,6],df_disk[,7],"disk")
  disk<-setNames(disk,c("netp","nets","blk","mbw","llcmr","llcocc","cpu","category"))
  
  #importing network
  df_network <- read.csv2(paste(folder_source,"net100.csv", sep = ""), sep=";")
  net<-data.frame(df_network[,1],df_network[,2],df_network[,3],df_network[,4],df_network[,5],df_network[,6],df_network[,7],"net")
  net<-setNames(net,c("netp","nets","blk","mbw","llcmr","llcocc","cpu","category"))
  
  
  total <- rbind(cpu, mem, disk, net, cache)#, cache_miss)
  rm(df_cache, cache, df_network, net, df_disk, disk, df_cpu, cpu, df_memory, mem, df_cache_miss, cache_miss)
  
  return(total)
  }