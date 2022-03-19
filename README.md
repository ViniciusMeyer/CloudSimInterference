# CloudSimInterference

CloudSimInterference is an extension of famous cloud computing simulator namely the CloudSim Toolkit, introduceced in [**CloudSim: a toolkit for modeling and simulation of cloud
computing environments and evaluation of resource provisioning algorithms**](http://www.buyya.com/papers/CloudSim2010.pdf). After that, many CloudSim extensions have been developed, and the one we have extended here is called [ContainerCloudSim](https://github.com/decheng-zhang/cloudsim-container), introduced in [**ContainerCloudSim: An environment for modeling and simulation of containers in cloud data centers**](https://onlinelibrary.wiley.com/doi/10.1002/spe.2422).

CloudSimInterference is an outcome from an extensive research process, producing the paper **IADA: A Dynamic Interference-Aware Cloud Scheduling Architecture for Latency-sensitive Workloads**, submitted to [**Journal of Systems and Softwares (JSS)**](https://www.journals.elsevier.com/journal-of-systems-and-software). At this moment, the current paper is under-review.



Here, you will find all used codes and results from the submitted paper.</br>

Authors: Vinícius Meyer, Matheus L. da Silva, Dionatrã F. Kirchoff, and César A. F. De Rose</br> 
Polytechnic School, Pontifical Catholic University of Rio Grande do Sul (PUCRS)- Porto Alegre, Brazil</br> 

# Instalation

Requirements:
- Java JDK 1.7 or above.
- R-base 3.6 or above.

Java libraries are included in this repository. However, some R packages need to be installed, such as:
- e1071
- caret
- stringr
- dplyr
- fossil
- ipred
- ocp
- rJava

This can be easly accomplished through the following R command:
```console
install.packages(c("e1071", "caret", "stringr", "dplyr", "fossil", "ipred", "ocp", "rJava"))
```

If you have any question, please do not exitate to contact us through vinicius.meyer@edu.pucrs.br</br>
