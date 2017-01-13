import Ember from 'ember';
import moment from 'moment';

export default Ember.Controller.extend({
    needs: ['application'],
    user: Ember.computed('model.user', function() {
  		return this.get("model.user");
  	})
});
