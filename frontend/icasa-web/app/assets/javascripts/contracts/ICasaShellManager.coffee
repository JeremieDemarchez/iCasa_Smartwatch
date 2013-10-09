###*
#  iCasa-Simulator Data Model ICasa Shell Manager Service Contract definition.
#  Used to call icasa commands to the backend.
#
# @author Issac GARCIA
###
define(() ->
    class ICasaShellManager

      ###*
      # Returns the frontend version.
      ###
      exec : (name, params) ->
        # keep it empty



    return new ICasaShellManager();
);