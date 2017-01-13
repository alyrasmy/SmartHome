import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
	needs: ['application'],
	loginFailed: false,
	requestFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
	loading: false,
	isAdmin:true, //if your registering you are therefore an admin

	actions: {
		  register: function() {
				if ( this.get("name") == null || this.get("username") == null ||
				this.get("password") == null || this.get("cPassword") == null ||
				this.get("email") == null  || this.get("houseId") == null ||
				this.get("roomIds") == null) {
					this.set("loginFailed", true);
				} else {
					if (this.get("password") == this.get("cPassword")) {
						this.setProperties({
							loginFailed: false,
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
								data: this.getProperties("name", "username","password","email","houseId","roomIds","isAdmin"),
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
						this.set("loginFailed", true);
					}
				}
		  },

		  success: function(self,response) {
		    self._actions.reset(self);
				self.set("loginFailed", false);
				self.set("requestFailed", false);
				self.set('loading', false);
				self.set("controllers.application.isRegistered", true);
				self.transitionToRoute("dashboard");
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
				this.transitionToRoute("dashboard");
				this.set("controllers.application.isRegistered", true);
			}
		}
});
