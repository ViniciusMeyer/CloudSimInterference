avg_classifier_level<-function(a,x,y){

  

cpu_a<-1
cpu_b<-20
cpu_c<-50
  
mem_a<-1
mem_b<-20
mem_c<-50

disk_a<-0
disk_b<-5
disk_c<-15

net_a<-1
net_b<-33
net_c<-66

cache_a<-1
cache_b<-33
cache_c<-66



  
    df<-data.frame()

  cpu<-""
  mem<-""
  disk<-""
  net<-""
  cache<-""

  
    
    
      cpu_percent<-mean(a$cpu[x:y])
    if(cpu_percent<cpu_a){
      cpu="abs"
    }else if(cpu_percent>=cpu_a && cpu_percent<cpu_b){
      cpu="low"
    }else if(cpu_percent>=cpu_b && cpu_percent<=cpu_c){
      cpu="mod"
    }else if(cpu_percent>cpu_c){
      cpu="hig"
    }
    
    mem_percent<-mean(a$mbw[x:y])
    if(mem_percent<mem_a){
      mem="abs"
    }else if(mem_percent>=mem_a && mem_percent<mem_b){
      mem="low"
    }else if(mem_percent>=mem_b && mem_percent<=mem_c){
      mem="mod"
    }else if(mem_percent>mem_c){
      mem="hig"
    }
    
    disk_percent<-mean(a$blk[x:y])
    if(disk_percent==disk_a){
      disk="abs"
    }else if(disk_percent>disk_a && disk_percent<disk_b){
      disk="low"
    }else if(disk_percent>=disk_b && disk_percent<=disk_c){
      disk="mod"
    }else if(disk_percent>disk_c){
      disk="hig"
    }
    
    net_percent<-mean(a$netp[x:y])
    if(net_percent<net_a){
      net="abs"
    }else if(net_percent>=net_a && net_percent<net_b){
      net="low"
    }else if(net_percent>=net_b && net_percent<=net_c){
      net="mod"
    }else if(net_percent>net_c){
      net="hig"
    }
    
    cache_percent<-mean(a$llcocc[x:y])
    if(cache_percent<cache_a){
      cache="abs"
    }else if(cache_percent>=cache_a && cache_percent<cache_b){
      cache="low"
    }else if(cache_percent>=cache_b && cache_percent<=cache_c){
      cache="mod"
    }else if(cache_percent>cache_c){
      cache="hig"
    }
    
    
    
      
    
    
    row <- data.frame(interval=0, resource="cpu", per=cpu)
    df <- bind_rows(df, row)
    row <- data.frame(interval=0, resource="mem", per=mem)
    df <- bind_rows(df, row)
    row <- data.frame(interval=0, resource="disk", per=disk)
    df <- bind_rows(df, row)
    row <- data.frame(interval=0, resource="net", per=net)
    df <- bind_rows(df, row)
    row <- data.frame(interval=0, resource="cache", per=cache)
    df <- bind_rows(df, row)
    
      
    
  
  
  #print(v_cpu)
  
  return(df)
  
  
}
