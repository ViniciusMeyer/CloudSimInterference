library(ggplot2)


#CPU
df_cpu <- read.csv2("/home/vinicius/interference/r/thesis_p3/nt_reads/CPU/cpu_24_min_2.csv", sep=";")
cpu<-data.frame(df_cpu[,1],df_cpu[,2],df_cpu[,3],df_cpu[,4],df_cpu[,5],df_cpu[,6],df_cpu[,7])
cpu<-setNames(cpu,c("netp","nets","blk","mbw","llcmr","llcocc","cpu"))

#workload
for(x in 0:11){
  inicio<-x*7200
  fim<-(x+1)*7200
  
  fileConn<-file(paste("~/interference/r/thesis_p3/nt_reads/workloads/cpu_",x,".csv",sep=""),"w")
  
  for(i in inicio:fim){
    writeLines(paste(cpu[i,1],cpu[i,2],cpu[i,3],cpu[i,4],cpu[i,5],cpu[i,6],cpu[i,7], sep = ";"),fileConn)
  }
    close(fileConn)
}



#Memory
df_mem <- read.csv2("/home/vinicius/interference/r/thesis_p3/nt_reads/memory/mem_24_min_3.csv", sep=";")
mem<-data.frame(df_mem[,1],df_mem[,2],df_mem[,3],df_mem[,4],df_mem[,5],df_mem[,6],df_mem[,7])
mem<-setNames(mem,c("netp","nets","blk","mbw","llcmr","llcocc","cpu"))

#workload
for(x in 0:11){
  inicio<-x*7200
  fim<-(x+1)*7200
  
  fileConn<-file(paste("~/interference/r/thesis_p3/nt_reads/workloads/memory_",x,".csv",sep=""),"w")
  
  for(i in inicio:fim){
    writeLines(paste(mem[i,1],mem[i,2],mem[i,3],mem[i,4],mem[i,5],mem[i,6],mem[i,7], sep = ";"),fileConn)
  }
  close(fileConn)
}

#Disk
df_disk <- read.csv2("/home/vinicius/interference/r/thesis_p3/nt_reads/disk/disk_24_min.csv", sep=";")
disk<-data.frame(df_disk[,1],df_disk[,2],df_disk[,3],df_disk[,4],df_disk[,5],df_disk[,6],df_disk[,7])
disk<-setNames(disk,c("netp","nets","blk","mbw","llcmr","llcocc","cpu"))

#workload
for(x in 0:11){
  inicio<-x*7200
  fim<-(x+1)*7200
  
  fileConn<-file(paste("~/interference/r/thesis_p3/nt_reads/workloads/memory_",x,".csv",sep=""),"w")
  
  for(i in inicio:fim){
    writeLines(paste(mem[i,1],disk[i,2],disk[i,3],disk[i,4],disk[i,5],disk[i,6],disk[i,7], sep = ";"),fileConn)
  }
  close(fileConn)
}







dframe_a <- read.csv2("/home/vinicius/interference/r/thesis_p3/nt_reads/workloads/memory_2.csv", sep=";")
time_a<- seq(1,nrow(dframe_a))
a<-data.frame(time_a,dframe_a[,1],dframe_a[,2],dframe_a[,3],dframe_a[,4],dframe_a[,5],dframe_a[,6],dframe_a[,7])
a<-setNames(a,c("time","netp","nets","blk","mbw","llcmr","llcocc","cpu"))

total<- a


ggplot(total,aes(x=time)) +
  #geom_line(aes(y=llcmr,color="llcmr")) +
  #geom_smooth(aes(y=llcmr,color="llcmr"),method="auto", se=TRUE, fullrange=FALSE, level=0.99) + 
  geom_line(aes(y=netp,color="netp")) + 
  #geom_smooth(aes(y=netp,color="netp"),method="auto", se=TRUE, fullrange=FALSE, level=0.99) + 
  geom_line(aes(y=nets,color="nets")) +
  #geom_smooth(aes(y=nets,color="nets"),method="auto", se=TRUE, fullrange=FALSE, level=0.99) + 
  geom_line(aes(y=blk,color="blk")) +
  #geom_smooth(aes(y=blk,color="blk"),method="auto", se=TRUE, fullrange=FALSE, level=0.99) + 
  geom_line(aes(y=mbw,color="mbw")) +
  #geom_smooth(aes(y=mbw,color="mbw"),method="auto", se=TRUE, fullrange=FALSE, level=0.99) + 
  geom_line(aes(y=llcocc,color="llcocc")) +
  #geom_smooth(aes(y=llcocc,color="llcocc"),method="auto", se=TRUE, fullrange=FALSE, level=0.99) + 
  geom_line(aes(y=cpu,color="cpu")) +
  #geom_smooth(aes(y=cpu,color="cpu"),method="auto", se=TRUE, fullrange=FALSE, level=0.99) + 
  scale_color_manual(values = c(
    'llcmr' = 'darkgray',
    'netp' = 'darkblue',  
    'nets' = 'red',
    'blk' = 'green',
    'mbw' = 'blue',
    'llcocc' = 'orange',
    'cpu' = 'black')) +
  labs(color = 'Resource', title="Application", x="Time (hour)", y="Interference (%)") +
  theme_bw() + 
  scale_x_continuous(breaks= seq(0, max(total$time), by =600))+
  scale_y_continuous(limits=c(0, 100))+
  theme(text=element_text(family="Times")) +
  #guides(colour = guide_legend(nrow = 1)) +
  theme(legend.position = "right")


