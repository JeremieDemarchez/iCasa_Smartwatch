require([
  'jquery',
  'jquery.ui',
  'text!templates/scriptPlayer.html',
  'domReady',
],
($, ui, ko) ->

  $( "#date" ).datepicker({
    dateFormat: "dd/mm/yy",
    firstDay: 1,
    gotoCurrent: true
  });

  $( "#scriptStart" ).datepicker({
    dateFormat: "dd/mm/yy",
    firstDay: 1,
    gotoCurrent: false
  });

  $( "#scriptSlider" ).slider({
    min: 0,
    max: 100,
    value: 0
  });

  $( "#timeSpeed" ).change(() ->
    if $(this).val() > $("#maxSpeed").val()
      $(this).val($("#maxSpeed").val())
    if $(this).val() < 0
      $(this).val(0)
    $("#timeSpeedSlider").slider("value", $(this).val());
  );

  $( "#hour" ).change(() ->
    if $(this).val() < 0
      $(this).val(0)
    if $(this).val() > 23
      $(this).val(23)
  );

  $( "#minute" ).change(() ->
    if $(this).val() < 0
      $(this).val(0)
    if $(this).val() > 59
      $(this).val(59)
  );

  $( "#scriptPlayerDialog" ).dialog({
    autoOpen: false,
    width:505
  });

  $( "#scriptPlayerDialogBtn" ).click(() ->
    $( "#scriptPlayerDialog" ).dialog( "open" );
  );
  $( ".scriptLineCell" ).click(() ->
    $( ".scriptLineCell" ).each(() ->
      $( this ).parent().css("background", "#FFFFFF");
    );
    $(this).parent().css("background", "#E5E5E5");
    $( "#scriptPlayerDialogForm" ).children().remove();
  );

  $( "#scriptPlayerDialogCb").change(() ->
    $( ".scriptPlayerDialogBoxes" ).attr("checked", $(this).is(':checked'));
  );

  $( "#scriptPlayerDialogDelete").click(() ->
    $( ".scriptPlayerDialogBoxes:checked" ).parent().parent().remove();
  );

  $( "#scriptPlayerDialogBtn" ).hide();
);