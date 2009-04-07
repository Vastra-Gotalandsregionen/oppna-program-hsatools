/**
 * /* Copyright 2009 Västa Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *  */
 */
using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Web;
using System.Web.SessionState;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.UI.HtmlControls;

namespace development.templates
{
	/// <summary>
	/// Summary description for ListAllPages.
	/// </summary>
	public class input : System.Web.UI.Page
	{
		protected System.Web.UI.WebControls.Label enhetLabel;
  		protected System.Web.UI.WebControls.Label caretypeLabel;
        protected System.Web.UI.WebControls.Label muncipalityLabel;
        protected System.Web.UI.WebControls.Label searchTypeLabel;
        protected System.Web.UI.WebControls.PlaceHolder textPlaceHolder; 

	    /// <summary>
	    /// Page_Load anropas n�r sidan laddas. Kontrollerar inparametrar, tar fram data och binder kontrollen som visar data.
	    /// </summary>
	    /// <param name="sender">Sidan.</param>
	    /// <param name="e">Argument till sidan.</param>
		private void Page_Load(object sender, System.EventArgs e)
		{
            textPlaceHolder.Visible = false;

            if (Request.Form["unitname"] != null)
            {
                enhetLabel.Text = Request.Form["unitname"];
                textPlaceHolder.Visible = true;
            }
            else
                enhetLabel.Text = "Ej ifyllt.";

            if (Request.Form["caretype"] != null)
            {
                caretypeLabel.Text = Request.Form["caretype"];
                textPlaceHolder.Visible = true;
            }
            else
                caretypeLabel.Text = "Ej valt.";

            if (Request.Form["muncipality"] != null)
            {
                muncipalityLabel.Text = Request.Form["muncipality"];
                textPlaceHolder.Visible = true;
            }
            else
                muncipalityLabel.Text = "Ej valt.";

            if (Request.Form["SearchType"] != null)
            {
                searchTypeLabel.Text = Request.Form["SearchType"];
                textPlaceHolder.Visible = true;
            }

            enhetLabel.DataBind();
            caretypeLabel.DataBind();
            muncipalityLabel.DataBind();
        }


		#region Web Form Designer generated code
		override protected void OnInit(EventArgs e)
		{
			//
			// CODEGEN: This call is required by the ASP.NET Web Form Designer.
			//
			InitializeComponent();
			base.OnInit(e);
		}
		
		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{    
			this.Load += new System.EventHandler(this.Page_Load);
		}
		#endregion
	}
}
