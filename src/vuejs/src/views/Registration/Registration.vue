<template>
  <div class="registration">
    <b-form @submit="onSubmit" class="formInsR">
      <div class="container registrationContainer">
        <div class="row">
          <div class="col-12">
            <div class="card cardR">
              <div class="card-body">
                <div class="card-title mb-4">
                  <h3 class="titleReg">Registration</h3>
                </div>
                <div class="row">
                  <div class="col-12">
                    <hr/>
                    <div class="tab-content ml-1" id="myTabContent">
                      <div class="tab-pane fade show active" id="basicInfo"
                        role="tabpanel" aria-labelledby="basicInfo-tab">
                          <div class="row">
                            <div class="col-sm-2 col-md-2 col-4">
                              <label class="labelText">Username</label>
                            </div>
                            <div class="col-md-8 col-8">
                              <b-form-input
                                id="input-username"
                                v-model="form.username"
                                required
                                type="text"
                                @focusout="onBlurUser"
                                placeholder="Insert username"
                                aria-describedby="user-help-block"
                              ></b-form-input>
                              <b-form-text id="user-help-block" v-show="!correctUser">
                                Username already in use
                              </b-form-text>
                            </div>
                          </div>
                          <hr/>
                          <div class="row">
                            <div class="col-sm-2 col-md-2 col-4">
                              <label class="labelText">Email</label>
                            </div>
                            <div class="col-md-8 col-8">
                              <b-form-input
                                id="input-email"
                                v-model="form.email"
                                type="email"
                                required
                                @focusout="onBlurEmail"
                                placeholder="Insert email"
                                aria-describedby="email-help-block"
                              ></b-form-input>
                              <b-form-text id="email-help-block" v-show="!correctEmail">
                                Email already in use
                              </b-form-text>
                            </div>
                          </div>
                          <hr/>
                          <div class="row">
                            <div class="col-sm-2 col-md-2 col-4">
                              <label class="labelText">Password</label>
                            </div>
                            <div class="col-md-8 col-8">
                              <b-form-input
                                id="input-password"
                                v-model="form.password"
                                type="password"
                                required
                                @focusout="onBlurPsw"
                                placeholder="Insert password"
                                aria-describedby="password-help-block"
                              ></b-form-input>
                              <button id="buttonHideShowPsw" title="Hold down to show the password"
                                @click="changeTypePsw" type = "button"></button>
                            </div>
                          </div>
                          <div class="row pers">
                            <div class="col-sm-2 col-md-2 col-4">
                              <label class="labelText">Repeat password</label>
                            </div>
                            <div class="col-md-8 col-8">
                              <b-form-input
                                id="re-input-password"
                                v-model="form.repassword"
                                type="password"
                                required
                                @focusout="onBlurRePsw"
                                placeholder="Insert password"
                                aria-describedby="password-help-block"
                              ></b-form-input>
                              <button id="buttonHideShowRePsw" title="Hold down to show the password"
                              @click="changeTypeRePsw" type = "button"></button>
                            </div>
                          </div>
                          
                          <hr/>
                          <div class="text-center buttonsDiv">
                            <b-button role="button" variant="outline-primary" type="submit">
                              Register</b-button> 
                          </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </b-form>
  </div>
</template>

<script src="./Registration.js">
</script>


<style lang="sass">
  @import './registration'
</style>
