$(document).ready(function () {
  var windowWidthHero = $(window).width();

  $(".heroBanner-welcome .hero__mask").each(function () {
    if ($(this).attr("data-mask") == "dark") {
      $(this)
        .find(".mask__logo")
        .attr("src", "./components/heroBanner__welcome/img/newLight.png")
        .addClass("mask__logo--white");
    } else {
      $(this)
        .find(".mask__logo")
        .attr("src", "./components/heroBanner__welcome/img/newDark.png")
        .addClass("mask__logo--dark");
    }
  });

  createHeroSlider();
  arrowSliderStyle();

  //Extra Hashtag Logic
  extraHash($(".heroBanner-welcome .hero__lables"));

  // HERO WITH MODAL START
  $(".heroBanner-welcome .heroBanner__slide--type3 .hero__button").click(
    function () {
      $(".heroBanner__modal").css("display", "flex").hide().fadeIn();
      var modalHeight = $(".heroBanner__modal").css("height");
      $("main").css({ height: modalHeight, overflow: "hidden" });
      $("footer").hide();
      $("header").hide();
    }
  );

  $(".heroBanner-welcome .modal__close").click(function () {
    $(".heroBanner__modal").css("display", "flex").fadeOut();
    $("main").css({ height: "auto", overflow: "auto" });
    $("footer").show();
    $("header").show();
  });

  // HERO WITH MODAL END

  // More Hashtag Logic
  $(window).on("resize", function () {
    if ($(window).width() !== windowWidthHero) {
      windowWidthHero = $(window).width();
      createHeroSlider();
      extraHash($(".heroBanner-welcome .hero__info"));
    }
  });
});

/****************************************************
 * FUNCTIONS
 ****************************************************/

/* CREATE SLIDER
  ============================= */

var createHeroSlider = function () {
  // destroy and initialize again
  if ($(".heroBanner-welcome .swiper-slide-active").length > 0) {
    $(".heroBanner-welcome .heroBanner__container")[0].swiper.destroy();
    addBg($(window).width());
  }

  if ($(".heroBanner-welcome .heroBanner__wrapper").children().length == 1) {
    $(".heroBanner__pagination").hide();
    $(".heroBanner__arrows").hide();
    if ($(".heroBanner-welcome .swiper-slide-active").length > 0) {
      $(".heroBanner-welcome .heroBanner__container")[0].swiper.destroy();
      addBg($(window).width());
    }
  } else {
    if ($(window).width() > 1025) {
      heroBannerSwiper = new Swiper(
        ".heroBanner-welcome .heroBanner__container",
        {
          loop: true,
          slidesPerGroup: 1,
          autoResize: true,
          simulateTouch: false,
          autoplay: {
            delay: 4000,
          },
          loopFillGroupWithBlank: true,
          slidesPerView: "auto",
          on: {
            transitionEnd: function () {
              var active = $(".heroBanner-welcome .swiper-slide-active").attr(
                "data-slide-number"
              );
              $(".heroBanner-welcome [data-number] .current").text(active);
              arrowSliderStyle();
            },
          },
          // Navigation arrows
          navigation: {
            nextEl: ".heroBanner-welcome [data-next-small]",
            prevEl: ".heroBanner-welcome [data-prev-small]",
          },
        }
      );
    } else {
      heroBannerSwiper = new Swiper(
        ".heroBanner-welcome .heroBanner__container",
        {
          spaceBetween: 10,
          autoplay: {
            delay: 4000,
          },
          paginationClickable: true,
          autoResize: true,
          pagination: {
            el: ".heroBanner-welcome [data-pagination]",
            clickable: true,
          },
        }
      );
    }

    var active = $(".heroBanner-welcome .swiper-slide-active").attr(
      "data-slide-number"
    );
    var total = $(
      ".heroBanner-welcome .swiper-slide:not(.swiper-slide-duplicate)"
    ).length;
    $(".heroBanner-welcome [data-number] .current").text(active);
    $(".heroBanner-welcome [data-number] .total").text(total);
    oneSliderFix($(".heroBanner-welcome"), heroBannerSwiper);
  }
};

/* SLIDER ARROW COLORS LOGIC
  ============================= */

function arrowSliderStyle() {
  if ($(window).width() > 1025) {
    if ($(".swiper-slide-active .hero__mask").attr("data-mask") == "dark") {
      $(".heroBanner-welcome .icon-down").css("color", "#ffffff");
      $(".heroBanner-welcome .total").css("color", "#ffffff");
      $(".heroBanner-welcome [data-number]").css("color", "#ffffff");
    } else {
      $(".heroBanner-welcome .icon-down").css("color", "#00a2c1");
      $(".heroBanner-welcome .total").css("color", "#262626");
      $(".heroBanner-welcome [data-number]").css("color", "#262626");
    }
  }
}
