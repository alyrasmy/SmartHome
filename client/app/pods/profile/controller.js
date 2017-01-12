import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	needs: ['application'],
	submitFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	editMode:false,
	isAdmin: false,

	user: Ember.computed('model.user', function() {
		return this.get("model.user");
	}),

	userHouseId: Ember.computed('model.user', function() {
		return this.get("model.user").get("house").slice()[0].get("id");
	}),

	actions: {
		submit: function() {
				var self = this;
				if ( this.get("name") == null || this.get("username") == null ||
				this.get("oldPassword") == null || this.get("newPassword") == null ||
				this.get("email") == null  || this.get("houseId") == null ||
				this.get("roomIds") == null) {
					this.set("submitFailed", true);
				} else {
					if (this.get("oldPassword") == this.get("user").get("password")) {
						this.setProperties({
							submitFailed: false,
							isProcessing: true
						});
						this.set("timeout", setTimeout(this._actions.slowConnection.bind(this), 5000));
						var host = this.store.adapterFor('application').get('host'),
								namespace = this.store.adapterFor('application').namespace,
								postUrl = [ host, namespace, 'user/create' ].join('/');
						var request = $.post(postUrl, this.getProperties("name", "username","newPassword","email","houseId","roomIds","isAdmin"));
						this.set('loading', true);
						request.then(this._actions.success.bind(this), this._actions.failure.bind(this));
					} else { // both password fields are not equal. Throw invalid input warning
						this.set("submitFailed", true);
					}
				}
		  },

		  success: function() {
				var self = this;
		    this_actions.reset(self);
				this.set("submitFailed", false);
				this.set('loading', false);
				this.set("editMode",false);
		  },

		  failure: function() {
				var self = this;
		    this._actions.reset(self);
				this.set('loading', false);
		    this.set("submitFailed", true);
		  },

		  slowConnection: function() {
		    this.set("isSlowConnection", true);
		  },

		  reset: function(self) {
		    clearTimeout(self.get("timeout"));
		    self.setProperties({
		      isProcessing: false,
		      isSlowConnection: false
		    });
		  },

			cancel: function() {
				this.set("editMode",false);
			},

			editable: function() {
				this.set("editMode",true);
				this.get("user");
			}
		}
});
