import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	needs: ['application'],
	submitFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	requestFailed: false,
	editMode:false,
	isAdmin: true,

	user: Ember.computed('model.user', function() {
		return this.get("model.user");
	}),

	name: Ember.computed('model.user', function() {
		return this.get("model.user").get("name");
	}),

	email: Ember.computed('model.user', function() {
		return this.get("model.user").get("email");
	}),

	username: Ember.computed('model.user', function() {
		return this.get("model.user").get("username");
	}),

	userHouseId: Ember.computed('model.user', function() {
		return this.get("model.user").get("house").slice()[0].get("id");
	}),

	userRooms: Ember.computed('model.user', function() {
		var self = this;
		var modelRooms = this.get("model.user").get("rooms");
		var rooms = '';
		modelRooms.forEach(function(room) {
			rooms += room.get('id');
			rooms += ",";
		})

		return rooms;
	}),

	actions: {
		submit: function() {
				var self = this;
				if ( this.get("name") == null || this.get("username") == null ||
				this.get("oldPassword") == null || this.get("newPassword") == null ||
				this.get("email") == null  || this.get("userHouseId") == null ||
				this.get("userRooms") == null) {
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
								this.set('loading', true);
								var self = this;
								$.ajax({
										url: postUrl,
										type: "POST",
										data: this.getProperties("name", "username","newPassword","email","userHouseId","userRooms","isAdmin"),
										dataType: 'text',
										async: true,
										success: function (response) {
												self._actions.success(self,response);
										},
										error: function (jqXHR, textStatus, errorThrown) {
												self._actions.failure(self);
										}
								});
					} else { // both password fields are not equal. Throw invalid input warning
						this.set("submitFailed", true);
					}
				}
		  },

			success: function(self,response) {
				self._actions.reset(self);
				self.set("requestFailed", false);
				self.set("submitFailed", false);
				self.set('loading', false);
				this.set("editMode",false);
			},

		  failure: function(self) {
		    self._actions.reset(self);
				self.set('loading', false);
		    self.set("requestFailed", true);
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
				this.set("requestFailed", false);
			},

			editable: function() {
				this.set("editMode",true);
				this.get("user");
			}
		}
});
