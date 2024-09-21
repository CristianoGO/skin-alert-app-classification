# skin-alert-app-classification

## Instruções do projeto

Neste projeto, você irá desenvolver um aplicativo em Java para classificar imagem dermatoscópica como sendo melanoma ou carcinoma basocelular.
 
### Sistema operacional

* Linux Ubuntu na versão 24.04.1 LTS
 
### Linguagem de programação 

* Java

### Ambiente de desenvolvimento

* Android Studio (versão 2021.2.1 Chipmunk)
   
### Requisitos nessesário

* Transferência indutiva (Transfer learning)
* MobileNetV2
* Cross-validation

### Considerações finais

* O modelo foi treinado com 30 épocas, o que pode torna o processo de treinamento demorado, mas isso vai depender das configurações do seu computador
* Após o treinamento de cada época é gerada uma matriz de confusão como na imagem abaixo:
  
  ![Matriz de confusão para cada fold](https://github.com/CristianoGO/model-deep-learning-classified/blob/main/coding/01-mobileNetV2/fold_30/Screenshot%20from%202024-09-21%2015-20-55.png)

* Finalizado o treinamento é gerado os resultados para cada fold como mostra a imagem abaixo:

  ![Resultados do treinamento do modelo para cada fold](https://github.com/CristianoGO/model-deep-learning-classified/blob/main/coding/01-mobileNetV2/fold_30/Screenshot%20from%202024-09-21%2015-21-19.png)

### Segue essas orientações e faça as mudanças necessárias de acordo seu projeto para que os resultados seram obtidos. 
