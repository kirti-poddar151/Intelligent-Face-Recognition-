# Intelligent-Face-Recognition-
**Innovation**

* Facial Recognition with 3-D modelling using multi-model Convolutional Neural Networks.
* A Secure and Faster facial recognition with Multi Dimensional training. (Introduced Multiple trainings for a face.)
* A User friendly recognition technique which improves with every recognition by clicking pictures when recognize face.
* A reliable face recognition technique with a confidence score attached with every recognition.
* An effective face recognition technique which handles similar face conflicts by training fake users at registration time.

# Need for new face recognition algorithm

* Existing Face Algorithms works on matrix matching. Since the Face matrix for two users may or may not differ depends upon their similarity in faces.
* Some Reliable Face Algorithms like Apple’s FACE-ID can easily crack-able. You just need to know the user’s Pin/Pattern. Whenever you open other’s phone with pin/pattern, the Face-ID will capture your face as a reliable user and after a while, it started unlocking with your face.

# Features

* **Multi-Training:** There are multiple algorithms resides which recognize face using single training, But we are introducing a multi-training, multi-model face recognition algorithm.
* **Similar Face Conflict:** There are multiple algorithms exist that check similar face conflicts but none of them are correct and accurate as they use face matrix for recognition. But In our approach, we manually as well as automatically train fake faces at run time and extend dataset with multi training.
* **Faster vs Accurate face recognition:** The faster face recognition implies face recognition with edge detection. Whereas in the accurate face recognition, We match edges, face, and eyes.
* **Block Training:** Using the concept of blockchain, we save face data in a block on each attempt of unlocking and retrieve the data when we unlock the phone. This increases the usability of data as we don’t need to register face again, Also efficiency as blockchain is faster than secure key storage.

# Face Matching Algorithm
In Face Matching Algorithm we divided complete face algorithm into Four steps :-

* **Face Detection:** In Face Detection Phase we detect an object (In our case that is a face) and create an ‘X’ dimensional circle around the face. Using the concept of neighbor point allocation in circular radius we create 𝑁∗𝑁−1 points on the circle. Also, we create an X, Y grid of 𝑃∗𝑃 Dimensional where P denotes the number of Cells. Finally, we set the middle value as threshold using which we set a new binary value. We set 1 for values equal or higher than the threshold and 0 for values lower than the threshold.

![image](https://user-images.githubusercontent.com/80633197/184526792-aa0953be-4f51-4a27-a8b1-1b9a773b7c02.png)

* **Algorithm Training:** We capture Multiple pictures using face detection techniques and train images with the same size and parameters with label=1. We also pass multiple negative images to train the model with better accuracy. In the training method, if we pass any unequal size image, the algorithm will automatically resize the image and use it as a train part with label=1. We train a model using CNN. We extract the histograms of each image based on the number of grids (X and Y) passed by parameter. After extracting the histogram of each region, we concatenate all histograms and create a new one that will be used to represent the image. The images, labels all will be saved in data structures so at face recognition time we can compare all of it with a new face.
![image](https://user-images.githubusercontent.com/80633197/184526789-1d8b54ef-dafa-49b7-89e8-49b2bf9b46d4.png)

* **Face Recognition:** To predict a new image we just need to call the Predict function passing the image as a parameter. The Predict function will extract the histogram from the new image, compare it to the histograms stored in the data structure which is our trained model, and predict the output.
* **Confidence Score:** As we know the confidence score is very much important for an algorithm to identify whether the training was done correctly or not. Using the concept of histogram distance in the chi-square test we will find the distance between the trained model and newly face.

	![image](https://user-images.githubusercontent.com/80633197/184526709-4d21b0b0-75ea-4781-abf6-a02199f074ca.png)

More the distance is, Lesser the confidence and vice-versa

![image](https://user-images.githubusercontent.com/80633197/184526744-d00a8d46-713b-45cd-a17f-7f9fb6fa75eb.png)


# OverFlow Flow For Registrantion Module

![image](https://user-images.githubusercontent.com/80633197/184526828-ebe4a3b4-bfcc-41b4-a179-d9c6e65f506c.png)

![image](https://user-images.githubusercontent.com/80633197/184526872-391679e2-e07e-4d32-91ac-fae13c1cc8a2.png)

![image](https://user-images.githubusercontent.com/80633197/184526897-d9914dd4-c330-4081-8e99-e1e41e720d6d.png)

# Apple Face Id and Google Face Id

**Apple Face Id :**

<br />Working: 
* Works on 3-D infrared Mapping and capture an image, and a dot projector will project out over 30,000 invisible infrared dots. 
* These dots create a neural network to create a mathematical model for a face.
* Face ID uses a "TrueDepth camera system", which consists of sensors, cameras, and a dot projector at the top of the iPhone display in the notch to create a detailed 3D map of your face

<br /> Disadvantages
* Apple’s liveness detection can be overcome quite easily, for instance by knowing that the 3D camera turns into 2D mode if the sunlight is too bright as the infrared cannot cope with it. Then you don’t need more than a printed image and a pair of glasses with two black dots glued to it in order to overcome the liveness detection.
* Hardware Dependent Feature. 
* Apple works with a FIDO principle, meaning the biometric data is stored solely on the device. The issues with this is: A) a client device can be manipulated easily and without the owner recognizing it. B)  your biometric template is not transferable for use on other devices.

![image](https://user-images.githubusercontent.com/80633197/184527100-ee3e2212-1e7a-461f-a891-5456588e2a11.png)

![image](https://user-images.githubusercontent.com/80633197/184527105-affc958b-7d49-42e3-8a1c-4314dcd4e401.png)

**Google Face Id**
<br /> Working:

* Google follow’s  motion-sensing radar and live transcription.
* Pixel 4 use an infrared sensor to project dots on your facial features that are then used to create a unique depth map of your face.
* Google's approach also hinges on a small chip inside the phone that senses your motion using radar (like picking up the phone) and hands off to the dot projector to do the unlocking work.

<br />Disadvantages
* Google’s image recognition is excellent, but in terms of face recognition their offering is fairly limited. They offer face detection but not matching or identification.
* Spoofing is easy in Google’s Face Id
* Hardware Dependent Solution

# Intelligent Recognition Face Id

**Samsung intelligent Recognition:**
<br /> Working
* Works on 3-D Facial features such as jaw line, eyes position, nose position, depth of nose, covered edges etc.
* A dynamic threshold value with a dynamic confidence score associated with each facial feature.
* Every facial feature contains a coefficient value which will be multiplied with it and create a deep denser neural network to create a mathematical model for face features.
* Depth Camera Measures The facial distance from camera and generates more facial data than a 2-D CNN model from a picture.

**Samsung intelligent Recognition:**
<br/> Solution for Challenges
* For sunny and shadow conditions, our face recognition algorithm automatically enables “Rhythm Camera System” which automatically adjust the camera aperture, focal length, exposure compensation and provides more reliable security.
* Software Dependent Feature. 
* Samsung works on trust zone principle, we encrypt our data and store in chipset’s trust zone so that no one can manipulate the data easily. And associate face data with “Samsung Pass” so that we can easily transfer for use on other device.

![image](https://user-images.githubusercontent.com/80633197/184527263-01619b70-a74c-4b1c-8b5b-43ca4a9d0045.png)
<br /> **2-D Facial Model**

![image](https://user-images.githubusercontent.com/80633197/184527270-dbbb46bc-171c-44ce-96bc-fbe9e21818ab.png)
