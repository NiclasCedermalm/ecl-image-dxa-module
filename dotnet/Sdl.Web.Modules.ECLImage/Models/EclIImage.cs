using Sdl.Web.Common.Configuration;
using Sdl.Web.Common.Models;
using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Globalization;
using System.Linq;
using System.Reflection.Emit;
using System.Web;

namespace Sdl.Web.Modules.ECLImage.Models
{

    /// <summary>
    /// ECL Image
    /// </summary>
    public class EclImage : EclItem
    {
        static readonly string ECL_STUB_SCHEMA_PREFIX = "ExternalContentLibraryStubSchema";

        static public bool UseTemplateFragment { get; private set; } = false;
        static public bool GenerateResponsiveImages { get; private set; } = false;

        /// <summary>
        /// Configure
        /// </summary>
        /// <param name="configuration"></param>
        public static void Configure(NameValueCollection configuration)
        {
            
            var useTemplateFragment = configuration["ecl-image-use-template-fragment"];
            if ( useTemplateFragment != null )
            {
                UseTemplateFragment = bool.Parse(useTemplateFragment);
            }

            // TODO: Call this useDxaAspectRatioCrop instead?
            var generateResponsiveImages = configuration["ecl-image-generate-responsive-images"];
            if ( generateResponsiveImages != null )
            {
                GenerateResponsiveImages = bool.Parse(generateResponsiveImages);
            }
        }

        /// <summary>
        /// Build and register sub type of an ECL image. Needs to be one per ECL stub schema.
        /// </summary>
        /// <param name="eclName"></param>
        public static void BuildAndRegisterSubType(string eclName)
        {
            var type = typeof(EclImage);

            var aName = new System.Reflection.AssemblyName("Sdl.Web.Modules.ECLImage");
            var ab = AppDomain.CurrentDomain.DefineDynamicAssembly(aName, AssemblyBuilderAccess.Run);
            var mb = ab.DefineDynamicModule(aName.Name);
            var tb = mb.DefineType(type.Name + "Proxy", System.Reflection.TypeAttributes.Public, type);

            var attrCtorParams = new Type[] { typeof(string), typeof(string) };
            var attrCtorInfo = typeof(SemanticEntityAttribute).GetConstructor(attrCtorParams);
            var attrBuilder = new CustomAttributeBuilder(attrCtorInfo, new object[] { ViewModel.CoreVocabulary, ECL_STUB_SCHEMA_PREFIX + eclName });
            tb.SetCustomAttribute(attrBuilder);

            ModelTypeRegistry.RegisterViewModel(new MvcData { ViewName = ECL_STUB_SCHEMA_PREFIX + eclName }, tb.CreateType());
        }

        /// <summary>
        /// To HTML
        /// </summary>
        /// <param name="widthFactor"></param>
        /// <param name="aspect"></param>
        /// <param name="cssClass"></param>
        /// <param name="containerSize"></param>
        /// <returns></returns>
        public override string ToHtml(string widthFactor, double aspect = 0, string cssClass = null, int containerSize = 0)
        {
            if (UseTemplateFragment && !string.IsNullOrEmpty(EclTemplateFragment))
            {
                return base.ToHtml(widthFactor, aspect, cssClass, containerSize);
            }
                     
            var imageUrl = Url;
            if ( imageUrl.StartsWith("/") && GenerateResponsiveImages )
            {
                imageUrl = SiteConfiguration.MediaHelper.GetResponsiveImageUrl(Url, aspect, widthFactor, containerSize);
            }
            
            // Right now this is connected to the DXA Whitelabel design. 
            //
            string dataAspect = (Math.Truncate(aspect * 100) / 100).ToString(CultureInfo.InvariantCulture);
            string widthAttr = string.IsNullOrEmpty(widthFactor) ? null : string.Format("width=\"{0}\"", widthFactor);
            string classAttr = string.IsNullOrEmpty(cssClass) ? null : string.Format("class=\"{0}\"", cssClass);
            return string.Format("<img src=\"{0}\" data-aspect=\"{1}\" {2}{3}/>",
                imageUrl, dataAspect, widthAttr, classAttr);             
            
        }

        /// <summary>
        /// Gets the default View.
        /// </summary>
        /// <param name="localization">The context Localization</param>
        /// <remarks>
        /// This makes it possible possible to render "embedded" Image Models using the Html.DxaEntity method.
        /// </remarks>
        public override MvcData GetDefaultView(Localization localization)
        {
            return new MvcData("ECLImage:Image");
        }
    }
}