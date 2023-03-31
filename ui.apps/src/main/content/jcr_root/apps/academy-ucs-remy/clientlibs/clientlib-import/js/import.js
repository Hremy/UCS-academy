$(document).ready(function () {

    $("#btnSubmit").css("display", "none");

    $('#csvFileArticle').change(function(){
           $('#selectedFile').text($('#csvFileArticle').val().replace(/C:\\fakepath\\/i, ''));

           var myBar = $("#progressBar");

           $("#btnSubmit").css("margin-top", "10px");
           $("#btnSubmit").css("display", "block");
           $("#resultMessage").html("");
           myBar.css("width", 0 + "%");
           myBar.html("");

            var i = 0;
            width = 0;
              if (false && i == 0) {
                i = 1;
                var id = setInterval(frame, 12);
                function frame() {
                  if (width >= 100) {
                    clearInterval(id);
                    i = 0;
                  } else {
                    width++;
                    myBar.css("width", width + "%");
                    myBar.html(width  + "%"+ " Uploading...");
                    myBar.css("background-color", "#cba90e");
                    if(width == 100) {
                        myBar.html(width  + "%"+ " Uploaded");
                    }
                  }
                }
              }

    });

    $("#btnSubmit").click(function (event) {

        if ($("#csvFileArticle").val().length > 1) {

            var filename = $("#csvFileArticle").val();
            var extension = filename.replace(/^.*\./, '');

            if (extension == filename) {
                extension = '';
            } else {
                extension = extension.toLowerCase();
            }

            if(extension != 'csv') {
                alert("Only csv format is allowed!");
                event.preventDefault();
                return;
            }

            event.preventDefault();

            var form = $('#fileUploadForm')[0];

            var data = new FormData(form);

            var isStarted = false;
            var isFinished = false;

            $("#btnSubmit").prop("disabled", true);

            $(".loading").removeClass("loading--hide").addClass("loading--show");
            $("#resultMessage").html("");

            $.ajax({
                type: "POST",
                enctype: 'multipart/form-data',
                url: "/bin/remy-ucs-related-articles-import-page",
                data: data,
                processData: false,
                contentType: false,
                cache: false,
                success: function (data) {

                    isFinished = true;

                    $("#resultMessage").html(data);
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $("#btnSubmit").prop("disabled", false);

                    myBar.css("width", 100 + "%");
                    myBar.html(100  + "%"+ " Completed");
                    myBar.css("background-color", "#04AA6D")

                },
                error: function (e) {

                    $(".result label").show();
                    $(".loading").removeClass("loading--show").addClass("loading--hide");
                    $("#btnSubmit").prop("disabled", false);

                }
            });

            var width = 0;
            var myBar = $("#progressBar");
            myBar.css("width", width + "%");
            myBar.html(width  + "%");
            frame();
            function frame() {
                $.ajax({
                    type: "GET",
                    url: "/bin/remy-ucs-related-articles-import-progress",
                    success: function (data) {

                      width = data['data'].progress;
                      var current = data['data'].current;
                      var total = data['data'].total;

                      console.log(width +"%");

                      if(!isStarted) {
                        if(width >= 100)  {
                            width = 0;
                        }else {
                            isStarted = true;
                        }
                      }

                      if(width == 100) isFinished = true;

                      if(isFinished) {

                        current = total;

                        myBar.css("width", 100 + "%");
                        myBar.html(100  + "%"+ " Completed");
                        myBar.css("background-color", "#04AA6D")

                      } else {

                        if(width == 0) {
                            myBar.css("width", 100 + "%");
                            myBar.html("Uploading...");
                            myBar.css("background-color", "#cba90e");

                        }else {
                            myBar.css("width", width + "%");
                            myBar.html(width  + "%"+ "");
                            myBar.css("background-color", "#04AA6D");
                        }

                        $("#resultMessage").html("PROCESSING... <br><br> Processed Articles: "+ current +" / "+ total);
                        $(".result label").show();

                        frame();
                      }

                    },
                    error: function (e) {

                    }
                });
            }

        } else {
            alert("Please, select CSV File to import");
            event.preventDefault();
            return;
        }

    });

});