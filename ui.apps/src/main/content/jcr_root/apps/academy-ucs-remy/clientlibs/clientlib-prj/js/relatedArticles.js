$(document).ready(function () {
    relatedArticles();
    extraHash($(".relatedArticles__articleCard"));
  });
  
  $(window).on("resize", function () {
    relatedArticles();
    extraHash($(".relatedArticles__articleCard"));
  });
  
  var relatedArticles = function () {
    var w = $(window).width();
  
    if (w <= 1024) {
      if ($(".relatedArticles .swiper-slide-active").length > 0) {
        // destroy and initialize again
        $(
          ".relatedArticles .relatedArticles__wrapper.swiper-container"
        )[0].swiper.destroy();
      }
  
      relatedArticlesSwiper = new Swiper(
        ".relatedArticles .relatedArticles__wrapper.swiper-container",
        {
          initialSlide: 0,
          paginationClickable: true,
          autoResize: true,
          pagination: {
            el: ".relatedArticles [data-pagination]",
            clickable: true,
          },
        }
      );
    } else {
      if ($(".relatedArticles .swiper-slide-active").length > 0) {
        $(
          ".relatedArticles .relatedArticles__wrapper.swiper-container"
        )[0].swiper.destroy();
      }
    }
  };