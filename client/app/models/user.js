import DS from "ember-data";

export default DS.Model.extend({
  name: DS.attr('string'),
  username: DS.attr('string'),
  password: DS.attr('string'),
  email: DS.attr('string'),
  isadmin: DS.attr('boolean'),
  house: DS.hasMany('room'),
  rooms: DS.hasMany('room'),
  tempthreshold: DS.attr('string'),
  humidthreshold: DS.attr('string'),
  ledthreshold: DS.attr('string'),
  city: DS.attr('string'),
  address: DS.attr('string'),
  camera: DS.attr('string')
});
