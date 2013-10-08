###*
#  iCasa-Simulator Data Model ICasa Manager Service Contract definition.
#  Used to know the backend version and the frontend version.
#
# @author Issac GARCIA
###
define(() ->
    class ICasaManager

      ###*
      # Returns the frontend version.
      ###
      getFrontendVersion : () ->
        # keep it empty

      ###*
      # Returns the backend version.
      ###
      getBackendVersion : () ->
        # keep it empty

    return new ICasaManager();
);