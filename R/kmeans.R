

#source("~/interference/r/categories/input_dataset.R")
#training_dataset_folder <- "~/interference/r/simulation/forced/"
#total<-input_dataset(training_dataset_folder)

if(firstTimeK==0){
cpu <- data.frame()
mem <- data.frame()
disk <- data.frame()
net <- data.frame()
cache <- data.frame()

  for(i in 1:nrow(total)){
    if(total[i,8]=="cpu"){
      cpu<-bind_rows(cpu, total[i,]) 
    }
    if(total[i,8]=="mem"){
      mem<-bind_rows(mem, total[i,]) 
    }
    if(total[i,8]=="disk"){
      disk<-bind_rows(disk, total[i,]) 
    }
    if(total[i,8]=="net"){
      net<-bind_rows(net, total[i,]) 
    }
    if(total[i,8]=="cache"){
      cache<-bind_rows(cache, total[i,]) 
    }
  }

  cl_cpu <- kmeans(cpu[1:7],3, nstart = 20)
  cl_mem <- kmeans(mem[1:7],3, nstart = 20)
  cl_disk <- kmeans(disk[1:7],3, nstart = 20)
  cl_net <- kmeans(net[1:7],3, nstart = 20)
  cl_cache <- kmeans(cache[1:7], 3, nstart = 20)
  firstTimeK=1;
  
}


#predict_cpu.kmeans(cl_cpu, cpu[20:40,1:7])


#cl_cpu$centers
#cl_mem$centers
#cl_disk$centers
#cl_net$centers
#cl_cache$centers

  
predict_cpu.kmeans <-function(object, newdata)
{
  predict.kmeans(object, newdata, 7)
}

predict_mem.kmeans <-function(object, newdata)
{
  predict.kmeans(object, newdata, 4)
}

predict_disk.kmeans <-function(object, newdata)
{
  predict.kmeans(object, newdata, 3)
}

predict_net.kmeans <-function(object, newdata)
{
  predict.kmeans(object, newdata, 1)
}

predict_cache.kmeans <-function(object, newdata)
{
  predict.kmeans(object, newdata, 6)
}



predict.kmeans <- function(object, newdata, var){
    low<-0
    mod<-0
    hig<-0
    centers <- object$centers
    n_centers <- nrow(centers)
   
    hig<-as.integer(which.max(centers[,var]))
    low<-as.integer(which.min(centers[,var]))
    
    for(i in 1:n_centers){
      if(low!=i && i!=hig){
        mod<-i
      }
    }

    result<- vector()
    a<-vector()
    dist_mat <- as.matrix(dist(rbind(centers, newdata)))
    dist_mat <- dist_mat[-seq(n_centers), seq(n_centers)]
    a<- max.col(-dist_mat)

    for(i in 1:length(a)){
      if(a[i]==low){
        result[i]<-"low"
      }else if(a[i]==mod){
        result[i]<-"mod"
      }else if(a[i]==hig){
        result[i]<-"hig"
      }
    }
    return(result)
  }

#icluster$betweenss/icluster$totss*100
#table(icluster$cluster[1],cpu[1:40,1:7])
  
#cl_total <- kmeans(total[1:7],3, nstart = 20)
#cl_total$betweenss/cl_total$totss*100


#citation("MASS")


#eee<-predict_cache.kmeans(cl_cpu, cpu[,1:7])
#cl_cpu$centers
#cl_cpu$betweenss/cl_cpu$totss*100
#cl_mem$betweenss/cl_mem$totss*100
#cl_disk$betweenss/cl_disk$totss*100
#cl_net$betweenss/cl_net$totss*100
#cl_cache$betweenss/cl_cache$totss*100



#Rand-Index
#install.packages("fossil")
# load package


#library(fossil)
#true_label <- as.numeric(total$category)

#true_label

# perform k-means clustering

#my_kmeans <- kmeans(x = total[,-8], centers = 3)

# clustering results
#my_kmeans$cluster
# Rand index
#rand.index(true_label, my_kmeans$cluster)

#Dist <- dist(total[,-8],method="euclidean")
#dunn(Dist, my_kmeans$cluster)

