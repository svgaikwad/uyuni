"use strict";

var React = require("react");

  var PopUp = React.createClass({
      propTypes: {
        id: React.PropTypes.string.isRequired, // the id of the html div tag
        onClosePopUp: React.PropTypes.func.isRequired, // a callback function with no parameters
        className: React.PropTypes.string, // the css className for the 'modal-dialog' div
        title: React.PropTypes.string,
        content: React.PropTypes.node.isRequired, // the body of the popup
        footer: React.PropTypes.node,
      },

    componentDidMount: function() {
      $("#" + this.props.id).on("hidden.bs.modal", this.props.onClosePopUp);
    },

    render: function() {
      return (
        <div className="modal fade" tabindex="-1" role="dialog" aria-labelledby="addPinPopUpLabel" id={this.props.id} >
          <div className={"modal-dialog " + (this.props.className ? this.props.className : "")}>
            <div className="modal-content">
              <div className="modal-header">
                <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                  <span aria-hidden="true">&times;</span>
                </button>
                {this.props.title ? <h4 className="modal-title">{this.props.title}</h4> : null}
              </div>
              <div className="modal-body">{this.props.content}</div>
              {this.props.footer ? <div className="modal-footer">{this.props.footer}</div> : null}
            </div>
          </div>
        </div>
      );
    }
  });

module.exports = {
    PopUp : PopUp
}
