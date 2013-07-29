###*
#  iCasa-Simulator Device Widget Service Contract definition.
#  You must implements this contract if you want to provide a new widget to iCasa Simulator Web application.
#
# @author Thomas Leveque
###
define(() ->
  class DeviceWidgetContract

    ###*
    # Returns the url to the base icon of this widget.
    # Icon format should be png file with defined transparency.
    # If returned URL starts with a slash (/), the URL is considered absolute.
    # Otherwise, it is considered relative to the location of the widget component file.
    # if undefined or null, use default device icon.
    ###
    getBaseIconURL : () ->
      # keep it empty

    ###*
    # Returns the url to the current icon of this widget.
    # The icon may change according to the current device state.
    # Icon format should be png file with defined transparency.
    # If returned URL starts with a slash (/), the URL is considered absolute.
    # Otherwise, it is considered relative to the location of the widget component file.
    # if undefined or null, always use base icon.
    ###
    getCurrentIconURL : () ->
      # keep it empty

    ###*
    # Returns true if the device icon may change according to the device state.
    # If false, method getCurrentIconURL will be not considered.
    ###
    manageDynamicIcon : () ->
      # keep it empty

    ###*
    # Returns true if this widget supports specified device.
    # @param {deviceServices} provided services by the device
    # @param {deviceType} type of the device
    ###
    manageDevice : (deviceServices, deviceType) ->
      # keep it empty

    ###*
    # Returns the url of the html template to use.
    # The template format must be a knockout and/or Handlerbar one.
    # If returned URL starts with a slash (/), the URL is considered absolute.
    # Otherwise, it is considered relative to the location of the widget component file.
    # if undefined or null, use default status window template.
    ###
    getStatusWindowTemplateURL : () ->
      # keep it empty

    ###*
    # Returns list of additional decorator objects that will be used.
    # if undefined or null, there will be no additional decorators.
    # A decorator object is a JSON object with following attributes: id, url, show.
    #  {id} unique id of the decorator
    #  {url} is the icon URL (could be relative or absolute if starts with a slash).
    #  {show} optional, false by default. if equals to true, visible at startup.
    ###
    getDecorators : () ->
      # keep it empty

    ###*
    # Allows to do initialization of the widget.
    # Be careful, you should not alterate the device view model but only subscribe notification from it.
    # In particular cases, you can update it if you want to perform actions on the device.
    # @param {deviceViewModel} knockout view model representing the device.
    ###
    init : (deviceViewModel) ->
      # keep it empty

  return new DeviceWidgetContract();
);
