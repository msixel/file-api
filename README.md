# FILE-API PROJECT

The project objective is to provide API to upload, compress and download files.

### API services

The rest service layer is composed by the following routes:

* **Uploading a file**.

  URL: `/api/file/v1/upload`

  Method: `POST`
  
  Content-type: `multipart/form-data`
  
  This service creates and returns a file UUID to be used in future calls. 
  
  In order to upload a file we can use the convenience page located at `http://localhost:8080/upload.html`.
  
  
* **Compressing previously uploaded file(s)**

  URL: `/api/file/v1/compress`

  Method: `POST`
  
  Content-type: `application/json`

  This service creates and returns a file UUID to be used in future calls. 

  Below we have an example of use.

	curl --location --request POST 'http://localhost:8080/api/file/v1/compress' --header 'Content-Type: application/json' \
	--data-raw '[
	    "9b859ab0-971f-4273-95f4-4b79cec293ab",
	    "7a6dd274-bfa9-4036-b26a-902fd86874c7",
	    "6f8dbd25-5519-4524-94ae-09f5c9e70cc4",
	    "3e54e632-7a96-484a-87bf-0b9c96c71d54"
	]'

  
* **Downloading a uploaded or compressed file**.

  URL: `/api/file/v1/download/{id}`

  Method: `GET`
    
  Content-type: `application/json`
  
  Below we have an example of use.
  
	curl --location --request GET 'localhost:8080/api/file/v1/download/0a513229-3957-4cba-9bde-defc8f98eb0a'

### building Java project

To build the project the following command line can be used.

    $> mvnw clean install


### Putting inside a docker container

To build a container image the following command line can be used.

    $> docker build -t sixel/file-api-docker .
    
To run the container the following command line can be used.

    $> docker run --name file-api -p 8080:8080 sixel/file-api-docker .


## Storage

All the files will be stored in a folder structure as the following:

	%TMPDIR%/
	    file-api/
	        upload/
	            <UUID>/
	                content-type.dat
	                bin/
	                    <Original filename>
	            <UUID>/
	                content-type.dat
	                bin/
	                    <Original filename>
	            <UUID>/
	                content-type.dat
	                bin/
	                    <Original filename>
	            <UUID>/
	                content-type.dat
	                bin/
	                    <Original filename>
    


    
### Future recommended improvements

Provide automatic tests in order to ensure high quality.

Use swagger in API layer to improve the documentation.

At download action is desired to provide the `Date` response header with the file creation time stamp.

Provide a service route to delete a file.

Provide a schedule routine to delete the oldest ones.
