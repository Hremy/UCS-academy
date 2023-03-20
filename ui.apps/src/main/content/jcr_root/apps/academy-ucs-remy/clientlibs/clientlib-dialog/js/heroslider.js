(function($, $document, Coral) {

    $document.on("dialog-ready", function() {

        const form = document.querySelector('form.cq-dialog');

        if(form) {

            const resourceType = form.querySelector('input[name="./sling:resourceType"]');

            if (resourceType && resourceType.value === "academy-ucs-remy/components/structure/heroslider") {

                const sliderPosition = form.querySelector('input[name="./sliderPosition"]');
                const sliderPosition1 = form.querySelector('coral-select[name="./sliderPosition1Slider"]');
                const sliderPosition2 = form.querySelector('coral-select[name="./sliderPosition2Slider"]');
                const sliderPosition3 = form.querySelector('coral-select[name="./sliderPosition3Slider"]');
                const sliderPosition4 = form.querySelector('coral-select[name="./sliderPosition4Slider"]');
                const sliderPosition5 = form.querySelector('coral-select[name="./sliderPosition5Slider"]');

                sliderPosition1.addEventListener("change", function(e) {
                    validateSliderPostion();
                });
                
                sliderPosition2.addEventListener("change", function(e) {
                    validateSliderPostion();
                });

                sliderPosition3.addEventListener("change", function(e) {
                    validateSliderPostion();
                });

                sliderPosition4.addEventListener("change", function(e) {
                    validateSliderPostion();
                });

                sliderPosition5.addEventListener("change", function(e) {
                    validateSliderPostion();
                });
 
                function validateSliderPostion() {
                    
                }

            }

        }

    });

})($, $(document), Coral);