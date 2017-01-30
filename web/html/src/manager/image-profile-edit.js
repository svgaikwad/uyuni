'use strict';

const React = require("react");
const ReactDOM = require("react-dom");
const Panel = require("../components/panel").Panel;
const Messages = require("../components/messages").Messages;
const Network = require("../utils/network");
const {SubmitButton, Button} = require("../components/buttons");

const typeMap = {
    "dockerfile": { name: "Dockerfile", storeType: "dockerreg" }
};

const msgMap = {
    "invalid_type": "Invalid image type."
};

class CreateImageProfile extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            imageTypes: [
                "dockerfile"
            ],
            messages: [],
            imageStores: [],
            imageType: "dockerfile",
            imageStore: "",
            path: "",
            label: "",
            token: ""
        };

        ["setValues", "handleChange", "handleImageTypeChange", "onUpdate", "onCreate",
            "clearFields", "getImageStores", "getTokens", "renderField", "renderTypeInputs",
            "renderImageTypeSelect", "renderStoreSelect", "renderButtons"]
                .forEach(method => this[method] = this[method].bind(this));

        this.getImageStores(typeMap[this.state.imageType].storeType);
        if(this.isEdit()) {
            this.setValues(profileId);
        }
    }

    isEdit() {
        return profileId ? true : false;
    }

    setValues(id) {
        Network.get("/rhn/manager/api/cm/imageprofiles/" + id).promise.then(res => {
            if(res.success) {
                var data = res.data;
                this.setState({
                    label: data.label,
                    token: data.token_id,
                    path: data.path,
                    imageType: data.image_type,
                    imageStore: data.store,
                    init_label: data.label
                });
            } else {
                window.location = "/rhn/manager/cm/imageprofiles/create";
            }
        });
    }

    handleChange(event) {
        const target = event.target;

        this.setState({
            [target.name]: target.value
        });
    }

    handleImageTypeChange(event) {
        this.handleChange(event);
        const val = event.target.value;

        this.getImageStores(typeMap[val].storeType);
    }

    onUpdate(event) {
        event.preventDefault();

        if(!this.isEdit()) {
            return false;
        }

        const payload = {
            label: this.state.label,
            path: this.state.path,
            imageType: this.state.imageType,
            storeLabel: this.state.imageStore
        };

        return Network.post(
            "/rhn/manager/api/cm/imageprofiles/" + profileId,
            JSON.stringify(payload),
            "application/json"
        ).promise.then(data => {
            if(data.success) {
                window.location = "/rhn/manager/cm/imageprofiles";
            } else {
                this.setState({
                    messages: <Messages items={data.messages.map(msg => {
                        return {severity: "error", text: msgMap[msg]};
                    })}/>
                });
            }
        });
    }

    onCreate(event) {
        event.preventDefault();

        if(this.isEdit()) {
            return false;
        }

        const payload = {
            label: this.state.label,
            path: this.state.path,
            imageType: this.state.imageType,
            storeLabel: this.state.imageStore
        };
        return Network.post(
            "/rhn/manager/api/cm/imageprofiles",
            JSON.stringify(payload),
            "application/json"
        ).promise.then(data => {
            if(data.success) {
                window.location = "/rhn/manager/cm/imageprofiles";
            } else {
                this.setState({
                    messages: <Messages items={data.messages.map(msg => {
                        return {severity: "error", text: msgMap[msg]};
                    })}/>
                });
            }
        });
    }

    clearFields() {
      this.setState({
          label: "",
          path: "",
          imageStore: ""
      });
    }

    getImageStores(type) {
        Network.get("/rhn/manager/api/cm/imagestores/type/" + type, "application/json").promise
            .then(data => {
                this.setState({
                    imageStores: data
                });
            });
    }

    getTokens() {

    }

    renderField(name, label, value, hidden = false, required = true) {
        return <div className="form-group">
            <label className="col-md-3 control-label">
                {label}
                { required ? <span className="required-form-field"> *</span> : undefined }
                :
            </label>
            <div className="col-md-6">
                <input name={name} className="form-control" type={hidden ? "password" : "text"} value={value} onChange={this.handleChange}/>
            </div>
        </div>;
    }

    renderTypeInputs(type, state) {
        switch (type) {
            case "dockerfile":
                return [
                    this.renderStoreSelect(),
                    this.renderField("path", t("Path"), this.state.path)
                ];
            default:
                return <div>if you see this please report a bug</div>;
        }
    }

    renderImageTypeSelect() {
        return <div className="form-group">
            <label className="col-md-3 control-label">Image Type<span className="required-form-field"> *</span>:</label>
            <div className="col-md-6">
               <select value={this.state.imageType} onChange={this.handleImageTypeChange} className="form-control" name="imageType" disabled={this.isEdit() ? "disabled" : undefined }>
                 {
                     this.state.imageTypes.map(k =>
                        <option key={k} value={k}>{ typeMap[k].name }</option>
                     )
                 }
               </select>
            </div>
        </div>;
    }

    renderStoreSelect() {
        return <div className="form-group">
            <label className="col-md-3 control-label">Target Image Store<span className="required-form-field"> *</span>:</label>
            <div className="col-md-6">
               <select value={this.state.imageStore} onChange={this.handleChange} className="form-control" name="imageStore">
                 <option key="0" disabled="disabled">{t("Select an image store")}</option>
                 {
                     this.state.imageStores.map(k =>
                        <option key={k.id} value={k.label}>{ k.label }</option>
                     )
                 }
               </select>
            </div>
        </div>;
    }

    renderButtons() {
        var buttons = [
            <Button id="clear-btn" className="btn-default pull-right" icon="fa-eraser" text={t("Clear fields")} handler={this.clearFields}/>
        ];
        if(this.isEdit()) {
            buttons.unshift(<SubmitButton id="update-btn" className="btn-success" icon="fa-edit" text={t("Update")}/>);
        } else {
            buttons.unshift(<SubmitButton id="create-btn" className="btn-success" icon="fa-plus" text={t("Create")}/>);
        }

        return buttons;
    }

    render() {
        return (
        <Panel title={this.isEdit() ? t("Edit Image Profile: '" + this.state.init_label + "'") : t("Create Image Profile")} icon="fa fa-pencil">
            {this.state.messages}
            <form className="image-profile-form" onSubmit={(e) => this.isEdit() ? this.onUpdate(e) : this.onCreate(e)}>
                <div className="form-horizontal">
                    { this.renderField("label", t("Label"), this.state.label) }
                    { this.renderImageTypeSelect() }
                    { this.renderTypeInputs(this.state.imageType) }
                    <div className="form-group">
                        <div className="col-md-offset-3 col-md-6">
                            { this.renderButtons() }
                        </div>
                    </div>
                </div>
            </form>
        </Panel>
        )
    }
}

ReactDOM.render(
  <CreateImageProfile />,
  document.getElementById('image-profile-edit')
)
