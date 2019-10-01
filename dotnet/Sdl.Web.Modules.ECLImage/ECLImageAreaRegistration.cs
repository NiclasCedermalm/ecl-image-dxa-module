using Sdl.Web.Common.Configuration;
using Sdl.Web.Common.Mapping;
using Sdl.Web.Common.Models;
using Sdl.Web.Modules.ECLImage.Models;
using Sdl.Web.Mvc.Configuration;
using System;
using System.Reflection.Emit;
using System.Web.Configuration;

namespace Sdl.Web.Modules.ECLImage
{
    /// <summary>
    /// ECL Image Area Registration
    /// </summary>
    public class ECLImageAreaRegistration : BaseAreaRegistration
    {

        public override string AreaName
        {
            get
            {
                return "ECLImage";
            }
        }

        protected override void RegisterAllViewModels()
        {
            var eclImageTypes = WebConfigurationManager.AppSettings["ecl-image-types"];
            if ( eclImageTypes != null )
            {
                foreach (var eclImageType in eclImageTypes.Split(new char[] {',', ' '}, StringSplitOptions.RemoveEmptyEntries))
                {
                    Sdl.Web.Common.Logging.Log.Info("Defining image type: " + eclImageType);
                    EclImage.BuildAndRegisterSubType(eclImageType.ToLower());
                }
            }
            RegisterViewModel("Image", typeof(EclImage));

            EclImage.Configure(WebConfigurationManager.AppSettings);
        }

    }
}
