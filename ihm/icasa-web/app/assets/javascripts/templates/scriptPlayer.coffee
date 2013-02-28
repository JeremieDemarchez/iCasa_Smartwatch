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
    gotoCurrent: true
  });

  d = new Date();
  $("#hour").val(d.getHours());
  $("#minute").val((d.getMinutes() < 10 ? '0' : '') + d.getMinutes());
  $("#date").datepicker("setDate", d);
  $("#scriptStart").datepicker("setDate", d);

  $( "#timeSpeedSlider" ).slider({
    min: 0,
    max: 30000,
    value: 1,
    slide: ( event, ui ) ->
      $( "#timeSpeed" ).val("x"+ui.value);
      $(ui.value).val($('#timeSpeed').val().replace("x",""));
  });

  $( "#scriptSlider" ).slider({
    min: d.valueOf(),
    max: d.valueOf(),
    value: d.valueOf()
  });

  $( "#timeSpeed" ).val("x" + $("#timeSpeedSlider").slider("value") );
  $( "#timeSpeed" ).change(() ->
    if $(this).val() > $("#maxSpeed").val()
      $(this).val("x" + $("#maxSpeed").val())
    if $(this).val() < 0
      $(this).val("x" + 0)
    $("#timeSpeedSlider").slider("value", $(this).val().replace("x",""));
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

  $( "#maxSpeed" ).change(() ->
    $( "#timeSpeedSlider" ).slider("option","max", $("#maxSpeed").val());
  );

  $( "#pause" ).click(() ->
    $("#timeSpeed").val("x" + 0);
  );

  $( "#play" ).click(() ->
    $("#timeSpeed").val("x" + $("#timeSpeedSlider").slider("value") );
  );

  $( "#scriptStart" ).change(() ->
    $( "#scriptSlider" ).slider("option","min", $(this).datepicker("getDate").valueOf());
  );

  $( "#scriptEnd" ).change(() ->
    $( "#scriptSlider" ).slider("option","max", $(this).datepicker("getDate").valueOf());
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

);