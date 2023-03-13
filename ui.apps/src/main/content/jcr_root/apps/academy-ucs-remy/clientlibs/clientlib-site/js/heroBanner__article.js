$(document).ready(function () {
  //Share open
  $(".heroBanner-article .share__button").click(function () {
    $(".heroBanner-article .share-list").fadeIn(200);
  });
  if ($(".heroBanner-article .hero__mask").attr("data-mask") == "dark") {
    $(".heroBanner-article .mask__logo")
      .attr("src", "./components/heroBanner__article/img/Logo_white.svg")
      .addClass("mask__logo--white");
  } else {
    $(".heroBanner-article .mask__logo")
      .attr("src", "./components/heroBanner__article/img/Logo_Black.svg")
      .addClass("mask__logo--dark");
  }

  $(document).on("click", ".share__print", function () {
    javascript:window.print()
  });
  //Share Close
  $(".heroBanner-article .share__close").click(function () {
    $(".heroBanner-article .share-list").fadeOut(200);
  });
});
